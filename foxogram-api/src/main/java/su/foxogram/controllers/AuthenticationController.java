package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.request.UserDeleteDTO;
import su.foxogram.dtos.request.UserLoginDTO;
import su.foxogram.dtos.request.UserResumeDTO;
import su.foxogram.dtos.request.UserSignUpDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.dtos.response.RefreshDTO;
import su.foxogram.dtos.response.TokenDTO;
import su.foxogram.models.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.*;
import su.foxogram.repositories.AuthorizationRepository;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.services.AuthenticationService;

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
	public TokenDTO signup(@Valid @RequestBody UserSignUpDTO body) throws UserWithThisEmailAlreadyExistException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER signup ({}, {}) request", username, email);

		User user = authenticationService.userSignUp(username, email, password);

		return new TokenDTO(user.getAccessToken());
	}

	@PostMapping("/login")
	public TokenDTO login(@Valid @RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER ({}) LOGIN request", email);

		User user = authenticationService.loginUser(email, password);

		return new TokenDTO(user.getAccessToken());
	}

	@PostMapping("/refresh")
	public RefreshDTO refreshToken(@RequestAttribute(value = "user") User user, @Valid @RequestBody UserResumeDTO body, HttpServletRequest request) throws UserUnauthorizedException, UserAuthenticationNeededException {
		Authorization auth = authorizationRepository.findById(user.getId());
		logger.info("TOKEN refresh for USER ({}, {}) request", user.getId(), user.getEmail());

		auth = authenticationService.refreshToken(user, auth);

		return new RefreshDTO(auth.getAccessToken(), user.getRefreshToken(), auth.getExpiresAt());
	}

	@PostMapping("/email/verify/{code}")
	public OkDTO emailVerify(@RequestAttribute(value = "user") User user, @PathVariable String code, HttpServletRequest request) throws CodeIsInvalidException, CodeExpiredException {
		logger.info("EMAIL verification for USER ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.verifyEmail(user, code);

		return new OkDTO(true);
	}

	@PostMapping("/logout")
	public OkDTO logout(@RequestAttribute(value = "user") User user, HttpServletRequest request) {
		Session session = sessionRepository.findByAccessToken(user.getAccessToken());
		logger.info("USER logout ({}, {}) request", user.getId(), user.getEmail());

		sessionRepository.delete(session);

		return new OkDTO(true);
	}

	@PostMapping("/delete/confirm/{code}")
	public OkDTO deleteConfirm(@RequestAttribute(value = "user") User user, @PathVariable String code, HttpServletRequest request) throws CodeIsInvalidException {
		logger.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.confirmUserDelete(user, code);


		return new OkDTO(true);
	}

	@PostMapping("/delete")
	public OkDTO delete(@RequestAttribute(value = "user") User user, @RequestBody UserDeleteDTO body, HttpServletRequest request) throws UserCredentialsIsInvalidException {
		String password = body.getPassword();
		logger.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.requestUserDelete(user, password);


		return new OkDTO(true);
	}
}
