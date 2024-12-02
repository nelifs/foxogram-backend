package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.dtos.request.UserDeleteDTO;
import su.foxogram.dtos.request.UserLoginDTO;
import su.foxogram.dtos.request.UserSignUpDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.dtos.response.TokenDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.AUTH, produces = "application/json")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/signup")
	public TokenDTO signup(@Valid @RequestBody UserSignUpDTO body) throws UserCredentialsDuplicateException, NoSuchAlgorithmException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		log.info("USER signup ({}, {}) request", username, email);

		String accessToken = authenticationService.userSignUp(username, email, password);

		return new TokenDTO(accessToken);
	}

	@PostMapping("/login")
	public TokenDTO login(@Valid @RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();
		log.info("USER ({}) LOGIN request", email);

		String accessToken = authenticationService.loginUser(email, password);

		return new TokenDTO(accessToken);
	}

	@PostMapping("/email/verify/{code}")
	public OkDTO emailVerify(@RequestAttribute(value = "user") User user, @PathVariable String code, HttpServletRequest request) throws CodeIsInvalidException, CodeExpiredException {
		log.info("EMAIL verification for USER ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.verifyEmail(user, code);

		return new OkDTO(true);
	}

	@PostMapping("/delete/confirm/{code}")
	public OkDTO deleteConfirm(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "accessToken") String accessToken, @PathVariable String code, HttpServletRequest request) throws CodeIsInvalidException {
		log.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.confirmUserDelete(user, code);

		return new OkDTO(true);
	}

	@PostMapping("/delete")
	public OkDTO delete(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "accessToken") String accessToken, @RequestBody UserDeleteDTO body, HttpServletRequest request) throws UserCredentialsIsInvalidException, CodeIsInvalidException {
		String password = body.getPassword();
		log.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.requestUserDelete(user, password, accessToken);

		return new OkDTO(true);
	}

	@PostMapping("/email/resend")
	public OkDTO resendEmail(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "accessToken") String accessToken, HttpServletRequest request) throws CodeIsInvalidException, NeedToWaitBeforeResendException {
		log.info("USER email verify resend requested ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.resendEmail(user, accessToken);

		return new OkDTO(true);
	}
}
