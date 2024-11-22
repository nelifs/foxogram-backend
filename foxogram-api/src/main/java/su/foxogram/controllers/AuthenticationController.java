package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.request.UserDeleteDTO;
import su.foxogram.dtos.request.UserLoginDTO;
import su.foxogram.dtos.request.UserSignUpDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.dtos.response.TokenDTO;
import su.foxogram.models.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.exceptions.*;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.services.AuthenticationService;

@RestController
@RequestMapping(value = APIConstants.AUTH, produces = "application/json")
public class AuthenticationController {
	private final AuthenticationService authenticationService;
	private final SessionRepository sessionRepository;

	final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService, SessionRepository sessionRepository) {
		this.authenticationService = authenticationService;
		this.sessionRepository = sessionRepository;
	}

	@PostMapping("/signup")
	public TokenDTO signup(@Valid @RequestBody UserSignUpDTO body) throws UserWithThisEmailAlreadyExistException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER signup ({}, {}) request", username, email);

		String accessToken = authenticationService.userSignUp(username, email, password);

		return new TokenDTO(accessToken);
	}

	@PostMapping("/login")
	public TokenDTO login(@Valid @RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();
		logger.info("USER ({}) LOGIN request", email);

		String accessToken = authenticationService.loginUser(email, password);

		return new TokenDTO(accessToken);
	}

	@PostMapping("/email/verify/{code}")
	public OkDTO emailVerify(@RequestAttribute(value = "user") User user, @PathVariable String code, HttpServletRequest request) throws CodeIsInvalidException, CodeExpiredException {
		logger.info("EMAIL verification for USER ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.verifyEmail(user, code);

		return new OkDTO(true);
	}

	@PostMapping("/logout")
	public OkDTO logout(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "accessToken") String accessToken, HttpServletRequest request) {
		Session session = sessionRepository.findByAccessToken(accessToken);
		logger.info("USER logout ({}, {}) request", user.getId(), user.getEmail());

		sessionRepository.delete(session);

		return new OkDTO(true);
	}

	@PostMapping("/delete/confirm/{code}")
	public OkDTO deleteConfirm(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "accessToken") String accessToken, @PathVariable String code, HttpServletRequest request) throws CodeIsInvalidException, UserUnauthorizedException {
		logger.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.confirmUserDelete(user, code, accessToken);


		return new OkDTO(true);
	}

	@PostMapping("/delete")
	public OkDTO delete(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "accessToken") String accessToken, @RequestBody UserDeleteDTO body, HttpServletRequest request) throws UserCredentialsIsInvalidException {
		String password = body.getPassword();
		logger.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.requestUserDelete(user, password, accessToken);


		return new OkDTO(true);
	}
}
