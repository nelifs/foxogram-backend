package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.models.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.*;
import su.foxogram.dtos.*;
import su.foxogram.repositories.cassandra.AuthorizationRepository;
import su.foxogram.repositories.cassandra.SessionRepository;
import su.foxogram.services.AuthenticationService;
import su.foxogram.utils.PayloadBuilder;

@RestController
@RequestMapping(value = APIEnum.AUTH, produces = "application/json")
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	private final SessionRepository sessionRepository;
	private final AuthorizationRepository authorizationRepository;
	final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService, SessionRepository sessionRepository, AuthorizationRepository authorizationRepository) {
		this.authenticationService = authenticationService;
		this.sessionRepository = sessionRepository;
		this.authorizationRepository = authorizationRepository;
	}

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody UserSignUpDTO body) throws EmailIsNotValidException, UserWithThisEmailAlreadyExistException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER signup ({}, {}) request", username, email);

		User user = authenticationService.userSignUp(username, email, password);

		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("username", user.getUsername()).addField("email", user.getEmail()).addField("accessToken", user.getAccessToken()).build());
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER ({}) LOGIN request", email);

		User user = authenticationService.loginUser(email, password);

		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "You have been successfully signed in!").addField("accessToken", user.getAccessToken()).build());
	}

	@PostMapping("/refresh")
	public ResponseEntity<String> refreshToken(@RequestBody UserResumeDTO body, HttpServletRequest request) throws UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		User user = authenticationService.getUser(request.getHeader("Authorization"), false, true);
		Authorization auth = authorizationRepository.findById(user.getId());
		logger.info("TOKEN refresh for USER ({}, {}) request", user.getId(), user.getEmail());

		auth = authenticationService.refreshToken(user, auth);

		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "Your token has been refreshed").addField("accessToken", auth.getAccessToken()).addField("refreshToken", user.getRefreshToken()).addField("expiresAt", String.valueOf(auth.getExpiresAt())).build());
	}

	@PostMapping("/email/verify/{code}")
	public ResponseEntity<String> emailVerify(@PathVariable String code, HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException, CodeIsInvalidException {
		User user = authenticationService.getUser(request.getHeader("Authorization"), false, false);
		logger.info("EMAIL verification for USER ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.verifyEmail(user, code);

		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "You have been successfully verified your email").build());
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException {
		User user = authenticationService.getUser(request.getHeader("Authorization"), true, false);
		Session session = sessionRepository.findByAccessToken(user.getAccessToken());
		logger.info("USER logout ({}, {}) request", user.getId(), user.getEmail());

		sessionRepository.delete(session);

		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "You have been successfully logged out").build());
	}

	@PostMapping("/delete/confirm/{code}")
	public ResponseEntity<String> deleteConfirm(@PathVariable String code, HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException, CodeIsInvalidException {
		User user = authenticationService.getUser(request.getHeader("Authorization"), false, false);
		logger.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.confirmUserDelete(user, code);

		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "You have successfully deleted your account!").build());
	}

	@PostMapping("/delete")
	public ResponseEntity<String> delete(@RequestBody UserDeleteDTO body, HttpServletRequest request) throws UserNotFoundException, UserEmailNotVerifiedException, UserAuthenticationNeededException, UserCredentialsIsInvalidException {
		String password = body.getPassword();
		User user = authenticationService.getUser(request.getHeader("Authorization"), false, false);
		logger.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.requestUserDelete(user, password);

		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "You need to confirm account deletion via email").build());
	}
}
