package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

	@Autowired
	public AuthenticationService(UserRepository userRepository, CodeRepository codeRepository, EmailService emailService, JwtService jwtService) {
		this.userRepository = userRepository;
		this.codeRepository = codeRepository;
		this.emailService = emailService;
		this.jwtService = jwtService;
	}

	public User getUser(String header, boolean checkIfEmailVerified) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		return validate(header.substring(7), checkIfEmailVerified);
	}

	public User validate(String token, boolean checkIfEmailVerified) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		String userId = jwtService.validate(token).getId();
		User user = userRepository.findById(Long.parseLong(userId));

		if (user == null) throw new UserUnauthorizedException();

		if (!user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED) && !checkIfEmailVerified) throw new UserEmailNotVerifiedException();

		return userRepository.findById(Long.parseLong(userId));
	}

	public String userSignUp(String username, String email, String password) throws UserWithThisEmailAlreadyExistException {
		if (userRepository.findByEmail(email) != null) throw new UserWithThisEmailAlreadyExistException();

		long id = new Snowflake(1).nextId();
		long createdAt = System.currentTimeMillis();
		long deletion = 0;
		String avatar = new Avatar("").getId();
		String accessToken = jwtService.generate(id);
		long flags = 0;
		int type = UserConstants.Type.USER.getType();
		password = Encryptor.hashPassword(password);

		User user = new User(id, avatar, username, email, password, createdAt, flags, type, deletion);

		userRepository.save(user);
		log.info("USER record saved ({}, {}) successfully", username, email);

		String emailType = EmailConstants.Type.CONFIRM.getValue();
		String digitCode = CodeGenerator.generateDigitCode();
		long expiresAt = System.currentTimeMillis() + CodesConstants.Lifetime.VERIFY.getValue();

		emailService.sendEmail(email, id, emailType, username, digitCode, expiresAt, accessToken);
		log.info("Sent EMAIL ({}, {}) to USER ({}, {})", type, digitCode, username, email);

		log.info("USER created ({}, {}) successfully", username, email);
		return accessToken;
	}

	public String loginUser(String email, String password) throws UserCredentialsIsInvalidException {
		User user = userRepository.findByEmail(email);

		if (user == null) throw new UserCredentialsIsInvalidException();

		String accessToken;
		if (Encryptor.verifyPassword(password, user.getPassword())) {
			log.info("USER SIGNED IN ({}, {}) successfully", user.getId(), email);
			accessToken = jwtService.generate(user.getId());
		}
		else throw new UserCredentialsIsInvalidException();
		
		return accessToken;
	}

	public void confirmUserDelete(User user, String pathCode) throws CodeIsInvalidException {
		Code code = codeRepository.findByValue(pathCode);

		if (code == null) {
			throw new CodeIsInvalidException();
		}

		long id = user.getId();

		userRepository.delete(user);
		log.info("USER record deleted ({}, {}) successfully", id, user.getEmail());
		codeRepository.delete(code);
		log.info("CODE record deleted ({}, {}) successfully", id, user.getEmail());
	}

	public void requestUserDelete(User user, String password, String accessToken) throws UserCredentialsIsInvalidException {
		if (Encryptor.verifyPassword(password, user.getPassword())) {

			long id = user.getId();
			String username = user.getUsername();
			String email = user.getEmail();
			String type = EmailConstants.Type.DELETE.getValue();
			String code = CodeGenerator.generateDigitCode();
			long expiresAt = System.currentTimeMillis() + CodesConstants.Lifetime.DELETE.getValue();

			emailService.sendEmail(email, id, type, username, code, expiresAt, accessToken);
			log.info("Sent EMAIL ({}, {}) to USER ({}, {})", type, code, id, email);
			log.info("USER deletion requested ({}, {}) successfully", id, email);
		} else throw new UserCredentialsIsInvalidException();
	}

	public void verifyEmail(User user, String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeRepository.findByValue(pathCode);

		if (code == null) {
			throw new CodeIsInvalidException();
		} else if (code.expiresAt <= System.currentTimeMillis()) {
			throw new CodeExpiredException();
		} else {
			user.addFlag(UserConstants.Flags.EMAIL_VERIFIED);
			userRepository.save(user);
			log.info("USER record updated ({}, {}) SET flags to EMAIL_VERIFIED", user.getId(), user.getEmail());
			codeRepository.delete(code);
			log.info("CODE record deleted ({}, {}) successfully", user.getId(), user.getEmail());

			log.info("EMAIL verified for USER ({}, {}) successfully", user.getId(), user.getEmail());
		}
	}
}
