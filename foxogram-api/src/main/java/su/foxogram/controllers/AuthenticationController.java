package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
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
@Tag(name = "Authentication")
@RequestMapping(value = APIConstants.AUTH, produces = "application/json")
public class AuthenticationController {
	private final AuthenticationService authenticationService;

	@Autowired
	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Operation(summary = "Register")
	@PostMapping("/register")
	public TokenDTO register(@Valid @RequestBody UserSignUpDTO body) throws UserCredentialsDuplicateException, NoSuchAlgorithmException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		log.info("USER register ({}, {}) request", username, email);

		String accessToken = authenticationService.userSignUp(username, email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Login")
	@PostMapping("/login")
	public TokenDTO login(@Valid @RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();
		log.info("USER ({}) LOGIN request", email);

		String accessToken = authenticationService.loginUser(email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Verify email")
	@PostMapping("/email/verify/{code}")
	public OkDTO emailVerify(@RequestAttribute(value = AttributesConstants.USER) User user, @PathVariable String code, HttpServletRequest request) throws CodeIsInvalidException, CodeExpiredException {
		log.info("EMAIL verification for USER ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.verifyEmail(user, code);

		return new OkDTO(true);
	}

	@Operation(summary = "Resend email")
	@PostMapping("/email/resend")
	public OkDTO resendEmail(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.ACCESS_TOKEN) String accessToken, HttpServletRequest request) throws CodeIsInvalidException, NeedToWaitBeforeResendException {
		log.info("USER email verify resend requested ({}, {}) request", user.getId(), user.getEmail());

		authenticationService.resendEmail(user, accessToken);

		return new OkDTO(true);
	}
}
