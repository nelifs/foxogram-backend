package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.enums.EmailEnum;
import su.foxogram.enums.TokenEnum;
import su.foxogram.repositories.EmailVerifyRepository;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.services.EmailService;
import su.foxogram.structures.Snowflake;
import su.foxogram.services.AuthorizationService;
import su.foxogram.util.Code;
import su.foxogram.util.Encryptor;
import su.foxogram.services.JwtService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = APIEnum.AUTH, produces = "application/json")
public class AuthController {

	private final EmailService emailService;
	private final AuthorizationService authorizationService;
	private final EmailVerifyRepository emailVerifyRepository;
	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;

	@Autowired
	public AuthController(EmailService emailService, AuthorizationService authorizationService, EmailVerifyRepository emailVerifyRepository, UserRepository userRepository, SessionRepository sessionRepository) {
		this.emailService = emailService;
		this.authorizationService = authorizationService;
		this.emailVerifyRepository = emailVerifyRepository;
		this.userRepository = userRepository;
		this.sessionRepository = sessionRepository;
	}

	@PostMapping("/create")
	public ResponseEntity<String> create(@RequestBody CreateRequest body) {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		User user;

		if (email.contains("@")) {
			user = userRepository.findByEmail(email);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RequestMessage().setSuccess(false).addField("message", "Email is not valid").build());
		}

		if (user == null) {
			return createUser(username, email, password);
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new RequestMessage().setSuccess(false).addField("message", "Account with this email already exist").build());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest body) {
		User user = userRepository.findByEmail(body.getEmail());

		if (user != null && !user.isEmailVerified()) {
			return authorizationService.handleNotVerifiedEmail();
		}

		if (user != null && Encryptor.verifyPassword(body.getPassword(), user.getPassword()) && user.isEmailVerified()) {
			return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have been successfully signed in!").build());
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestMessage().setSuccess(false).addField("message", "Invalid email or password!").build());
		}
	}

	@PostMapping("/resume")
	public ResponseEntity<String> resume(@RequestBody ResumeRequest body) {
		Session session = sessionRepository.findByResumeToken(body.getResumeToken());

		if (session == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestMessage().setSuccess(false).addField("message", "This session does not exist! You need to re-login").build());
		}

		if (session.isExpired()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestMessage().setSuccess(false).addField("message", "Your session has expired. You need to re-login").build());
		}

		String newResumeToken = JwtService.generate(session.getId(), TokenEnum.Type.RESUME_TOKEN, TokenEnum.Lifetime.RESUME_TOKEN);
		session.setResumeToken(newResumeToken);
		sessionRepository.save(session);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "Session has been successfully resumed").build());
	}

	@GetMapping("/email/verify/{code}")
	public ResponseEntity<String> emailVerify(@PathVariable String code, HttpServletRequest request) {
		EmailVerification verify = emailVerifyRepository.findByLetterCode(code);
		User user = authorizationService.getUser(request);

		if (verify != null && user != null) {
			return handleEmailVerification(user, verify);
		} else {
			verify = emailVerifyRepository.findByDigitCode(code);
			if (verify != null && user != null) {
				return handleEmailVerification(user, verify);
			} else {
				return handleInvalidCode();
			}
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
		User user = authorizationService.getUser(request);
		Session session = sessionRepository.findByAccessToken(user.getAccessToken());

		sessionRepository.delete(session);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have been successfully logged out").build());
	}


	@GetMapping("/delete/confirm/{code}")
	public ResponseEntity<String> deleteConfirm(@PathVariable String code, HttpServletRequest request) {
		EmailVerification verify = emailVerifyRepository.findByLetterCode(code);
		User user = authorizationService.getUser(request);

		if (verify != null && user != null) {
			return handleAccountDeletion(user, verify);
		} else {
			verify = emailVerifyRepository.findByDigitCode(code);
			if (verify != null && user != null) {
				return handleAccountDeletion(user, verify);
			} else return handleInvalidCode();
		}
	}

	@PostMapping("/delete")
	public ResponseEntity<String> delete(@RequestBody DeleteRequest body, HttpServletRequest request) {
		String password = body.getPassword();
		User user = authorizationService.getUser(request);

		if (Encryptor.verifyPassword(password, user.getPassword())) {
			long id = user.getId();
			String username = user.getUsername();
			String email = user.getEmail();
			String accessToken = user.getAccessToken();
			String type = EmailEnum.Type.DELETE.getValue();
			String digitCode = Code.generateDigitCode();
			String letterCode = Code.generateLetterCode();

			emailService.sendEmail(email, id, type, username, digitCode, letterCode, accessToken);

			return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You need to confirm account deletion via email").build());
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestMessage().setSuccess(false).addField("message", "Password is invalid!").build());
		}
	}

	private ResponseEntity<String> createUser(String username, String email, String password) {
		long id = new Snowflake(1).nextId();
		String avatar = new Avatar("").getEtag();
		boolean emailVerified = false;
		String accessToken = JwtService.generate(id, TokenEnum.Type.ACCESS_TOKEN, TokenEnum.Lifetime.ACCESS_TOKEN);
		long createdAt = System.currentTimeMillis();
		List<String> flags = new ArrayList<>();
		long deletion = 0;
		boolean disabled = false;
		boolean mfaEnabled = false;
		password = Encryptor.hashPassword(password);

		userRepository.save(new User(id, avatar, username, email, emailVerified, password, accessToken, createdAt, flags, deletion, disabled, mfaEnabled));

		String type = EmailEnum.Type.CONFIRM.getValue();
		String digitCode = Code.generateDigitCode();
		String letterCode = Code.generateLetterCode();

		emailService.sendEmail(email, id, type, username, digitCode, letterCode, accessToken);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("username", username).addField("email", email).addField("accessToken", accessToken).build());
	}

	private ResponseEntity<String> handleEmailVerification(User user, EmailVerification verify) {
		user.setEmailVerified(true);
		userRepository.save(user);
		emailVerifyRepository.delete(verify);

		long id = user.getId();
		String accessToken = user.getAccessToken();
		String email = user.getEmail();
		long createdAt = user.getCreatedAt();
		long expiresAt = createdAt + TokenEnum.Lifetime.RESUME_TOKEN.getValue();
		String resumeToken = JwtService.generate(id, TokenEnum.Type.RESUME_TOKEN, TokenEnum.Lifetime.RESUME_TOKEN);

		sessionRepository.save(new Session(id, accessToken, resumeToken, user.getCreatedAt(), expiresAt));

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You successfully verified your email").addField("email", email).addField("accessToken", accessToken).addField("resumeToken", resumeToken).build());
	}

	private ResponseEntity<String> handleAccountDeletion(User user, EmailVerification verify) {
		Session session = sessionRepository.findByAccessToken(user.getAccessToken());

		userRepository.delete(user);
		emailVerifyRepository.delete(verify);
		sessionRepository.delete(session);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have successfully deleted your account!").build());
	}

	private ResponseEntity<String> handleInvalidCode() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestMessage().setSuccess(false).addField("message", "Code is invalid!").build());
	}
}
