package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.*;
import su.foxogram.repositories.AuthorizationRepository;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.services.AuthorizationService;

@RestController
@RequestMapping(value = APIEnum.AUTH, produces = "application/json")
public class AuthController {

	private final AuthorizationService authorizationService;
	private final SessionRepository sessionRepository;
	private final AuthorizationRepository authorizationRepository;
	Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	public AuthController(AuthorizationService authorizationService, SessionRepository sessionRepository, AuthorizationRepository authorizationRepository) {
		this.authorizationService = authorizationService;
		this.sessionRepository = sessionRepository;
		this.authorizationRepository = authorizationRepository;
	}

	@PostMapping("/create")
	public ResponseEntity<String> create(@RequestBody CreateRequest body) throws EmailIsNotValidException, UserWithThisEmailAlreadyExistException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER create ({}, {}) request", username, email);

		User user = authorizationService.createUser(username, email, password);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("username", user.getUsername()).addField("email", user.getEmail()).addField("accessToken", user.getAccessToken()).build());
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER ({}) LOGIN request", email);

		authorizationService.loginUser(email, password);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have been successfully signed in!").build());
	}

	/*@PostMapping("/resume")
	public ResponseEntity<String> resumeSession(@RequestBody ResumeRequest body, HttpServletRequest request)
	throws UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		User user = authorizationService.getUser(request, false, true);

		authorizationService.resumeSession(user);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "otter").build());
	}*/

	@PostMapping("/refresh")
	public ResponseEntity<String> refreshToken(@RequestBody ResumeRequest body, HttpServletRequest request) throws UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		User user = authorizationService.getUser(request, false, true);
		Authorization auth = authorizationRepository.findById(user.getId());
		logger.info("TOKEN refresh for USER ({}, {}) request", user.getId(), user.getEmail());

		auth = authorizationService.refreshToken(user, auth);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "Your token has been refreshed").addField("accessToken", auth.getAccessToken()).addField("refreshToken", user.getRefreshToken()).addField("expiresAt", String.valueOf(auth.getExpiresAt())).build());
	}

	@PostMapping("/email/verify/{code}")
	public ResponseEntity<String> emailVerify(@PathVariable String code, HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException, CodeIsInvalidException {
		User user = authorizationService.getUser(request, false, false);
		logger.info("EMAIL verification for USER ({}, {}) request", user.getId(), user.getEmail());

		authorizationService.verifyEmail(user, code);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have been successfully verified your email").build());
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException {
		User user = authorizationService.getUser(request, true, false);
		Session session = sessionRepository.findByAccessToken(user.getAccessToken());
		logger.info("USER logout ({}, {}) request", user.getId(), user.getEmail());

		sessionRepository.delete(session);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have been successfully logged out").build());
	}

	@PostMapping("/delete/confirm/{code}")
	public ResponseEntity<String> deleteConfirm(@PathVariable String pathCode, HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException, CodeIsInvalidException {
		User user = authorizationService.getUser(request, false, false);
		logger.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		authorizationService.confirmUserDelete(user, pathCode);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have successfully deleted your account!").build());
	}

	@PostMapping("/delete")
	public ResponseEntity<String> delete(@RequestBody DeleteRequest body, HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException, UserCredentialsIsInvalidException {
		String password = body.getPassword();
		User user = authorizationService.getUser(request, false, false);
		logger.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		authorizationService.requestUserDelete(user, password);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You need to confirm account deletion via email").build());
	}
}
