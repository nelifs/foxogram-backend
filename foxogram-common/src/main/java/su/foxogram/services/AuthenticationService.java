package su.foxogram.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.constants.CodesConstants;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.exceptions.*;
import su.foxogram.models.Avatar;
import su.foxogram.models.Code;
import su.foxogram.models.User;
import su.foxogram.repositories.CodeRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;

@Slf4j
@Service
public class AuthenticationService {
	private final UserRepository userRepository;

	private final CodeRepository codeRepository;

	private final EmailService emailService;

	private final JwtService jwtService;

	private final APIConfig APIConfig;

	@Autowired
	public AuthenticationService(UserRepository userRepository, CodeRepository codeRepository, EmailService emailService, JwtService jwtService, APIConfig APIConfig) {
		this.userRepository = userRepository;
		this.codeRepository = codeRepository;
		this.emailService = emailService;
		this.jwtService = jwtService;
		this.APIConfig = APIConfig;
	}

	public User getUser(String header, boolean ignoreEmailVerification) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		long userId;

		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
					.setSigningKey(jwtService.getSigningKey())
					.build()
					.parseClaimsJws(header.substring(7));

			userId = Long.parseLong(claimsJws.getBody().getId());
		} catch (Exception e) {
			throw new UserUnauthorizedException();
		}

		User user = userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);

		if (!ignoreEmailVerification && user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED))
			throw new UserEmailNotVerifiedException();

		return userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);
	}

	public String userSignUp(String username, String email, String password) throws UserCredentialsDuplicateException {
		User user = createUser(username, email, password);
		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}
		log.info("USER created ({}, {}) successfully", username, email);

		sendConfirmationEmail(user);

		return jwtService.generate(user.getId());
	}

	private User createUser(String username, String email, String password) {
		long deletion = 0;
		String avatar = new Avatar("").getId();
		long flags = UserConstants.Flags.AWAITING_CONFIRMATION.getBit();
		int type = UserConstants.Type.USER.getType();

		return new User(0, avatar, null, username, email, Encryptor.hashPassword(password), flags, type, deletion, null);
	}

	private void sendConfirmationEmail(User user) {
		String emailType = EmailConstants.Type.EMAIL_VERIFY.getValue();
		String digitCode = CodeGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + CodesConstants.Lifetime.BASE.getValue();
		String accessToken = jwtService.generate(user.getId());

		emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), digitCode, issuedAt, expiresAt, accessToken);

		log.info("Sent EMAIL ({}, {}) to USER ({}, {})", emailType, digitCode, user.getUsername(), user.getEmail());
	}

	public String loginUser(String email, String password) throws UserCredentialsIsInvalidException {
		User user = findUserByEmail(email);
		validatePassword(user, password);

		return jwtService.generate(user.getId());
	}

	public User findUserByEmail(String email) throws UserCredentialsIsInvalidException {
		return userRepository.findByEmail(email).orElseThrow(UserCredentialsIsInvalidException::new);
	}

	private void validatePassword(User user, String password) throws UserCredentialsIsInvalidException {
		if (!Encryptor.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		log.info("PASSWORD VERIFIED FOR USER ({}, {})", user.getId(), user.getEmail());
	}

	public void verifyEmail(User user, String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = validateCode(pathCode);

		user.removeFlag(UserConstants.Flags.AWAITING_CONFIRMATION);
		user.addFlag(UserConstants.Flags.EMAIL_VERIFIED);
		userRepository.save(user);
		log.info("USER record updated ({}, {}) SET flags to EMAIL_VERIFIED", user.getId(), user.getEmail());
		log.info("EMAIL verified for USER ({}, {}) successfully", user.getId(), user.getEmail());

		if (!APIConfig.isDevelopment()) deleteVerificationCode(code);
	}

	public Code validateCode(String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeRepository.findByValue(pathCode);

		if (APIConfig.isDevelopment()) return null;

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
