package su.foxogram.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constants.UserConstants;
import su.foxogram.models.*;
import su.foxogram.constants.CodesConstants;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.TokenConstants;
import su.foxogram.exceptions.*;
import su.foxogram.repositories.CodeRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.structures.Snowflake;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;

@Service
public class AuthenticationService {
	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;
	private final CodeRepository codeRepository;
	private final EmailService emailService;
	private final JwtService jwtService;
	final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	public AuthenticationService(UserRepository userRepository, SessionRepository sessionRepository, CodeRepository codeRepository, EmailService emailService, JwtService jwtService) {
		this.userRepository = userRepository;
		this.sessionRepository = sessionRepository;
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
		String accessToken = jwtService.generate(id, TokenConstants.Lifetime.ACCESS_TOKEN);
		long flags = 0;
		int type = UserConstants.Type.USER.getType();
		password = Encryptor.hashPassword(password);

		User user = new User(id, avatar, username, email, password, createdAt, flags, type, deletion);

		userRepository.save(user);
		logger.info("USER record saved ({}, {}) successfully", username, email);

		String emailType = EmailConstants.Type.CONFIRM.getValue();
		String digitCode = CodeGenerator.generateDigitCode();
		long expiresAt = System.currentTimeMillis() + CodesConstants.Lifetime.VERIFY.getValue();

		emailService.sendEmail(email, id, emailType, username, digitCode, expiresAt, accessToken);
		logger.info("Sent EMAIL ({}, {}) to USER ({}, {})", type, digitCode, username, email);

		logger.info("USER created ({}, {}) successfully", username, email);
		return accessToken;
	}

	public String loginUser(String email, String password) throws UserCredentialsIsInvalidException {
		User user = userRepository.findByEmail(email);

		if (user == null) throw new UserCredentialsIsInvalidException();

		String accessToken;
		if (Encryptor.verifyPassword(password, user.getPassword()) && user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED)) {
			logger.info("USER SIGNED IN ({}, {}) successfully", user.getId(), email);
			accessToken = jwtService.generate(user.getId(), TokenConstants.Lifetime.ACCESS_TOKEN);
		}
		else throw new UserCredentialsIsInvalidException();
		
		return accessToken;
	}

	public void confirmUserDelete(User user, String pathCode, String accessToken) throws CodeIsInvalidException, UserUnauthorizedException {
		Code code = codeRepository.findByValue(pathCode);
		String userId = jwtService.validate(accessToken).getId();

		if (code == null) {
			throw new CodeIsInvalidException();
		}

		long id = user.getId();
		Session session = sessionRepository.findById(Long.parseLong(userId));

		userRepository.delete(user);
		logger.info("USER record deleted ({}, {}) successfully", id, user.getEmail());
		codeRepository.delete(code);
		logger.info("CODE record deleted ({}, {}) successfully", id, user.getEmail());
		sessionRepository.delete(session);
		logger.info("SESSION record deleted ({}, {}) successfully", id, user.getEmail());
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
			logger.info("Sent EMAIL ({}, {}) to USER ({}, {})", type, code, id, email);
			logger.info("USER deletion requested ({}, {}) successfully", id, email);
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
			logger.info("USER record updated ({}, {}) SET emailverify to TRUE", user.getId(), user.getEmail());
			codeRepository.delete(code);
			logger.info("CODE record deleted ({}, {}) successfully", user.getId(), user.getEmail());

			logger.info("EMAIL verified for USER ({}, {}) successfully", user.getId(), user.getEmail());
		}
	}
}
