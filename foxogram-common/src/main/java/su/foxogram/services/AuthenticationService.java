package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.UserConstants;
import su.foxogram.models.*;
import su.foxogram.constants.CodesConstants;
import su.foxogram.constants.EmailConstants;
import su.foxogram.exceptions.*;
import su.foxogram.repositories.CodeRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.structures.Snowflake;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;

@Slf4j
@Service
public class AuthenticationService {
	private final UserRepository userRepository;
	private final CodeRepository codeRepository;
	private final EmailService emailService;
	private final JwtService jwtService;
	private final APIConfig apiConfig;

	@Autowired
	public AuthenticationService(UserRepository userRepository, CodeRepository codeRepository, EmailService emailService, JwtService jwtService, APIConfig apiConfig) {
		this.userRepository = userRepository;
		this.codeRepository = codeRepository;
		this.emailService = emailService;
		this.jwtService = jwtService;
		this.apiConfig = apiConfig;
	}

    public User getUser(String header, boolean checkIfEmailVerified) throws UserUnauthorizedException, UserEmailNotVerifiedException {
        return validate(header.substring(7), checkIfEmailVerified);
    }

    public User validate(String token, boolean checkIfEmailVerified) throws UserUnauthorizedException, UserEmailNotVerifiedException {
        String userId = jwtService.validate(token).getId();

        User user = userRepository.findById(Long.parseLong(userId));

        if (user == null) throw new UserUnauthorizedException();
        if (!user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED) && !checkIfEmailVerified)
            throw new UserEmailNotVerifiedException();

        return user;
    }

    public String userSignUp(String username, String email, String password) throws UserWithThisUsernameOrEmailAlreadyExistException {
        User user = createUser(username, email, password);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserWithThisUsernameOrEmailAlreadyExistException();
        }
        log.info("USER created ({}, {}) successfully", username, email);

        if (!apiConfig.isDevelopment())
            sendConfirmationEmail(user);

        return jwtService.generate(user.getId());
    }

    private User createUser(String username, String email, String password) {
        long id = new Snowflake(1).nextId();
        long createdAt = System.currentTimeMillis();
        long deletion = 0;
        String avatar = new Avatar("").getId();
        long flags = apiConfig.isDevelopment() ? UserConstants.Flags.EMAIL_VERIFIED.getBit() : 0;
        int type = UserConstants.Type.USER.getType();

        return new User(id, avatar, username, email, Encryptor.hashPassword(password), createdAt, flags, type, deletion);
    }

    private void sendConfirmationEmail(User user) {
        String emailType = EmailConstants.Type.CONFIRM.getValue();
        String digitCode = CodeGenerator.generateDigitCode();
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + CodesConstants.Lifetime.VERIFY.getValue();
        String accessToken = jwtService.generate(user.getId());

        emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), digitCode, issuedAt, expiresAt, accessToken);

        log.info("Sent EMAIL ({}, {}) to USER ({}, {})", emailType, digitCode, user.getUsername(), user.getEmail());
    }

    public String loginUser(String email, String password) throws UserCredentialsIsInvalidException {
        User user = findUserByEmail(email);
        validatePassword(user, password);
        return generateAccessToken(user);
    }

    private User findUserByEmail(String email) throws UserCredentialsIsInvalidException {
        User user = userRepository.findByEmail(email);

        if (user == null)
            throw new UserCredentialsIsInvalidException();

        return user;
    }

    private void validatePassword(User user, String password) throws UserCredentialsIsInvalidException {
        if (!Encryptor.verifyPassword(password, user.getPassword()))
            throw new UserCredentialsIsInvalidException();

        log.info("PASSWORD VERIFIED FOR USER ({}, {})", user.getId(), user.getEmail());
    }

    private String generateAccessToken(User user) {
        String accessToken = jwtService.generate(user.getId());
        log.info("USER SIGNED IN ({}, {}) successfully", user.getId(), user.getEmail());
        return accessToken;
    }

    public void confirmUserDelete(User user, String pathCode) throws CodeIsInvalidException {
        if (!isCodeValid(pathCode))
            throw new CodeIsInvalidException();

        deleteUserAndCode(user, pathCode);
    }

    private boolean isCodeValid(String pathCode) {
        if (apiConfig.isDevelopment())
            return true;

        Code code = codeRepository.findByValue(pathCode);
        return code != null;
    }

    private void deleteUserAndCode(User user, String pathCode) {
        deleteUser(user);

        Code code = codeRepository.findByValue(pathCode);

        if (code != null)
            deleteVerificationCode(code);
    }

    private void deleteUser(User user) {
        userRepository.delete(user);
        log.info("USER record deleted ({}, {}) successfully", user.getId(), user.getEmail());
    }

    public void requestUserDelete(User user, String password, String accessToken) throws UserCredentialsIsInvalidException, CodeIsInvalidException {
        if (!Encryptor.verifyPassword(password, user.getPassword()))
            throw new UserCredentialsIsInvalidException();

        if (apiConfig.isDevelopment())
            confirmUserDelete(user, "0");
        else
            sendDeleteRequestEmail(user, accessToken);
    }

    private void sendDeleteRequestEmail(User user, String accessToken) throws CodeIsInvalidException {
        String emailType = EmailConstants.Type.DELETE.getValue();
        String code = CodeGenerator.generateDigitCode();
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + CodesConstants.Lifetime.DELETE.getValue();

        emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), code, issuedAt, expiresAt, accessToken);

        log.info("Sent EMAIL ({}, {}) to USER ({}, {})", emailType, code, user.getId(), user.getEmail());
        log.info("USER deletion requested ({}, {}) successfully", user.getId(), user.getEmail());
    }

    public void verifyEmail(User user, String pathCode) throws CodeIsInvalidException, CodeExpiredException {
        Code code = validateCode(pathCode);

        user.addFlag(UserConstants.Flags.EMAIL_VERIFIED);
        userRepository.save(user);
        log.info("USER record updated ({}, {}) SET flags to EMAIL_VERIFIED", user.getId(), user.getEmail());
        log.info("EMAIL verified for USER ({}, {}) successfully", user.getId(), user.getEmail());

        deleteVerificationCode(code);
    }

    private Code validateCode(String pathCode) throws CodeIsInvalidException, CodeExpiredException {
        Code code = codeRepository.findByValue(pathCode);

        if (code == null)
            throw new CodeIsInvalidException();

        if (code.expiresAt <= System.currentTimeMillis())
            throw new CodeExpiredException();

        return code;
    }

    private void deleteVerificationCode(Code code) {
        codeRepository.delete(code);
        log.info("CODE record deleted ({}, {}) successfully", code.getUserId(), code.getValue());
    }

    public void resendEmail(User user, String accessToken) throws CodeIsInvalidException, NeedToWaitBeforeResendException {
        Code code = codeRepository.findByUserId(user.getId());

        if (code == null) throw new CodeIsInvalidException();

        long issuedAt = code.getIssuedAt();
        if (System.currentTimeMillis() - issuedAt < CodesConstants.Lifetime.RESEND.getValue())
            throw new NeedToWaitBeforeResendException();

        emailService.sendEmail(user.getEmail(), user.getId(), code.getType(), user.getUsername(), code.getValue(), System.currentTimeMillis(), code.getExpiresAt(), accessToken);
    }
}
