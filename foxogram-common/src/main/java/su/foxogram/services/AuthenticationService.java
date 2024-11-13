package su.foxogram.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.models.*;
import su.foxogram.enums.CodesEnum;
import su.foxogram.enums.EmailEnum;
import su.foxogram.enums.TokenEnum;
import su.foxogram.exceptions.*;
import su.foxogram.repositories.AuthorizationRepository;
import su.foxogram.repositories.CodeRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.structures.Snowflake;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;
	private final AuthorizationRepository authorizationRepository;
	private final CodeRepository codeRepository;
	private final EmailService emailService;
	private final JwtService jwtService;
	final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	public AuthenticationService(UserRepository userRepository, SessionRepository sessionRepository, AuthorizationRepository authorizationRepository, CodeRepository codeRepository, EmailService emailService, JwtService jwtService) {
		this.userRepository = userRepository;
		this.sessionRepository = sessionRepository;
		this.authorizationRepository = authorizationRepository;
		this.codeRepository = codeRepository;
		this.emailService = emailService;
		this.jwtService = jwtService;
	}

	public User getUser(String header) throws UserUnauthorizedException {
		return validate(header.substring(7));
	}

	public User validate(String token) throws UserUnauthorizedException {
		User user = userRepository.findByAccessToken(token);
		if (user == null) throw new UserUnauthorizedException();

//		if (!user.isEmailVerified() && validateEmail) throw new UserEmailNotVerifiedException();
//
//		Session session = sessionRepository.findByAccessToken(user.getAccessToken());
//		if (session == null && validateSession) throw new UserAuthenticationNeededException();

		return userRepository.findByAccessToken(token);
	}

	public User userSignUp(String username, String email, String password) throws EmailIsNotValidException, UserWithThisEmailAlreadyExistException {
		if (userRepository.findByEmail(email) != null) throw new UserWithThisEmailAlreadyExistException();

		if (!email.contains("@")) throw new EmailIsNotValidException();

		long id = new Snowflake(1).nextId();
		long createdAt = System.currentTimeMillis();
		long deletion = 0;
		String avatar = new Avatar("").getId();
		String accessToken = jwtService.generate(id, TokenEnum.Type.ACCESS_TOKEN, TokenEnum.Lifetime.ACCESS_TOKEN);
		String refreshToken = jwtService.generate(id, TokenEnum.Type.REFRESH_TOKEN, TokenEnum.Lifetime.REFRESH_TOKEN);
		List<String> flags = new ArrayList<>();
		boolean emailVerified = false;
		boolean disabled = false;
		boolean mfaEnabled = false;
		password = Encryptor.hashPassword(password);

		User user = new User(id, avatar, username, email, emailVerified, password, accessToken, refreshToken, createdAt, flags, deletion, disabled, mfaEnabled);

		userRepository.save(user);
		logger.info("USER record saved ({}, {}) successfully", username, email);

		String type = EmailEnum.Type.CONFIRM.getValue();
		String digitCode = CodeGenerator.generateDigitCode();
		long expiresAt = System.currentTimeMillis() + CodesEnum.Lifetime.VERIFY.getValue();

		emailService.sendEmail(email, id, type, username, digitCode, expiresAt, accessToken);
		logger.info("Sent EMAIL ({}, {}) to USER ({}, {})", type, digitCode, username, email);

		logger.info("USER created ({}, {}) successfully", username, email);
		return user;
	}

	public User loginUser(String email, String password) throws UserCredentialsIsInvalidException {
		User user = userRepository.findByEmail(email);

		if (user == null) throw new UserCredentialsIsInvalidException();

		if (Encryptor.verifyPassword(password, user.getPassword()) && user.isEmailVerified())
			logger.info("USER SIGNED IN ({}, {}) successfully", user.getId(), email);
		else throw new UserCredentialsIsInvalidException();
		return user;
	}

	public void confirmUserDelete(User user, String pathCode) throws CodeIsInvalidException {
		Code code = codeRepository.findByValue(pathCode);

		if (code == null) {
			throw new CodeIsInvalidException();
		} else {
			long id = user.getId();
			Session session = sessionRepository.findByAccessToken(user.getAccessToken());

			userRepository.delete(user);
			logger.info("USER record deleted ({}, {}) successfully", id, user.getEmail());
			codeRepository.delete(code);
			logger.info("CODE record deleted ({}, {}) successfully", id, user.getEmail());
			sessionRepository.delete(session);
			logger.info("SESSION record deleted ({}, {}) successfully", id, user.getEmail());
		}
	}

	public void requestUserDelete(User user, String password) throws UserCredentialsIsInvalidException {
		if (Encryptor.verifyPassword(password, user.getPassword())) {
			long id = user.getId();
			String username = user.getUsername();
			String email = user.getEmail();
			String accessToken = user.getAccessToken();
			String type = EmailEnum.Type.DELETE.getValue();
			String code = CodeGenerator.generateDigitCode();
			long expiresAt = System.currentTimeMillis() + CodesEnum.Lifetime.DELETE.getValue();

			emailService.sendEmail(email, id, type, username, code, expiresAt, accessToken);
			logger.info("Sent EMAIL ({}, {}) to USER ({}, {})", type, code, id, email);
			logger.info("USER deletion requested ({}, {}) successfully", id, email);
		} else throw new UserCredentialsIsInvalidException();
	}

	public Authorization refreshToken(User user, Authorization auth) throws UserUnauthorizedException, UserAuthenticationNeededException {
		if (user == null) throw new UserUnauthorizedException();
		if (auth == null) throw new UserAuthenticationNeededException();

		long id = user.getId();
		TokenEnum.Lifetime accessTokenLifetime = TokenEnum.Lifetime.ACCESS_TOKEN;
		TokenEnum.Type accessTokenType = TokenEnum.Type.ACCESS_TOKEN;

		String newAccessToken = jwtService.generate(id, accessTokenType, accessTokenLifetime);

		TokenEnum.Lifetime refreshTokenLifetime = TokenEnum.Lifetime.ACCESS_TOKEN;
		TokenEnum.Type refreshTokenType = TokenEnum.Type.ACCESS_TOKEN;

		String newRefreshToken = jwtService.generate(id, refreshTokenType, refreshTokenLifetime);

		user.setAccessToken(newAccessToken);
		user.setRefreshToken(newRefreshToken);
		userRepository.save(user);
		logger.info("USER record updated ({}, {}) SET new REFRESH/ACCESS token", user.getId(), user.getEmail());

		long createdAt = System.currentTimeMillis();
		long expiresAt = createdAt + accessTokenLifetime.getValue();

		auth.setAccessToken(newAccessToken);
		auth.setCreatedAt(createdAt);
		auth.setExpiresAt(expiresAt);
		authorizationRepository.save(auth);
		logger.info("AUTH record updated ({}, {}) SET new ACCESS token", user.getId(), user.getEmail());

		logger.info("USER token refreshed ({}, {}) successfully", user.getId(), user.getEmail());
		return auth;
	}

	/*public Session resumeSession(User user) {
		Session session = sessionRepository.findByAccessToken(user.getAccessToken());

		return session;
	}*/

	public void verifyEmail(User user, String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeRepository.findByValue(pathCode);

		if (code == null) {
			throw new CodeIsInvalidException();
		} else if (code.expiresAt <= System.currentTimeMillis()) {
			throw new CodeExpiredException();
		} else {
			user.setEmailVerified(true);
			userRepository.save(user);
			logger.info("USER record updated ({}, {}) SET emailverify to TRUE", user.getId(), user.getEmail());
			codeRepository.delete(code);
			logger.info("CODE record deleted ({}, {}) successfully", user.getId(), user.getEmail());

			long id = user.getId() + new Snowflake(1).nextId();
			String accessToken = user.getAccessToken();
			long createdAt = user.getCreatedAt();
			long expiresAt = createdAt + TokenEnum.Lifetime.REFRESH_TOKEN.getValue();

			sessionRepository.save(new Session(id, accessToken, createdAt, expiresAt));
			logger.info("SESSION created ({}, {}) successfully", user.getId(), user.getEmail());
			logger.info("EMAIL verified for USER ({}, {}) successfully", user.getId(), user.getEmail());
		}
	}
}
