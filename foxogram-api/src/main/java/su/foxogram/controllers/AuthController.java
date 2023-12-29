package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.enums.TokenEnum;
import su.foxogram.repositories.EmailVerifyRepository;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.services.EmailService;
import su.foxogram.structures.AuthorizationController;
import su.foxogram.structures.Snowflake;
import su.foxogram.util.Code;
import su.foxogram.util.Encryptor;
import su.foxogram.services.JwtService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(APIEnum.AUTH)
public class AuthController {

	private final EmailService emailService;
	private final EmailVerifyRepository emailVerifyRepository;
	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;

	@Autowired
	public AuthController(
			EmailService emailService,
			EmailVerifyRepository emailVerifyRepository,
			UserRepository userRepository,
			SessionRepository sessionRepository) {
		this.emailService = emailService;
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
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new RequestMessage()
							.setSuccess(false)
							.addField("message", "Email is not valid")
							.build());
		}

		if (user == null) {
			return createUser(username, email, password);
		} else {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(new RequestMessage()
							.setSuccess(false)
							.addField("message", "Account with this email already exist")
							.build());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.getEmail());

		if (user != null && !user.isEmailVerified()) {
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body(new RequestMessage()
							.setSuccess(false)
							.addField("message", "You need to verify your email!")
							.build());
		}

		if (user != null && Encryptor.verifyPassword(loginRequest.getPassword(), user.getPassword()) && user.isEmailVerified()) {
			return ResponseEntity.ok(new RequestMessage()
							.setSuccess(true)
							.addField("message", "You have been successfully signed in!")
							.build());
		} else {
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body(new RequestMessage()
							.setSuccess(false)
							.addField("message", "Invalid email or password!")
							.build());
		}
	}

	@GetMapping("/email/verify/{code}")
	public ResponseEntity<String> emailVerify(@PathVariable String code, HttpServletRequest request) {
		EmailVerification verify = emailVerifyRepository.findByLetterCode(code);
		String token = request.getParameter("token");


		if (verify == null) {
			verify = emailVerifyRepository.findByDigitCode(code);
			if (verify != null) {
				return handleEmailVerification(code, token, verify);
			} else {
				return handleInvalidCode();
			}
		} else {
			return handleInvalidCode();
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

		String digitCode = Code.generateDigitCode();
		String letterCode = Code.generateLetterCode();
		boolean verified = false;

		emailVerifyRepository.save(new EmailVerification(id, digitCode, letterCode, verified));

		emailService.sendConfirmEmail(email, username, digitCode, letterCode, accessToken);

		return ResponseEntity.ok(new RequestMessage()
				.setSuccess(true)
				.addField("username", username)
				.addField("email", email)
				.addField("accessToken", accessToken)
				.build());
	}

	private ResponseEntity<String> handleEmailVerification(String code, String token, EmailVerification verify) {
		User user = userRepository.findByAccessToken(token);

		if (user == null) {
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body(new RequestMessage()
							.setSuccess(false)
							.addField("message", "You can only verify your email!")
							.build());
		}

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

		return ResponseEntity.ok(new RequestMessage()
				.setSuccess(true)
				.addField("message", "You successfully verified your email")
				.addField("email", email)
				.addField("accessToken", accessToken)
				.addField("resumeToken", resumeToken)
				.build());
	}

	private ResponseEntity<String> handleInvalidCode() {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new RequestMessage()
						.setSuccess(false)
						.addField("message", "Code is invalid!")
						.build());
	}
}
