package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.request.EmailCodeDTO;
import su.foxogram.dtos.request.UserLoginDTO;
import su.foxogram.dtos.request.UserSignUpDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.dtos.response.TokenDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

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
	public TokenDTO register(@Valid @RequestBody UserSignUpDTO body) throws UserCredentialsDuplicateException {
		String username = body.getUsername();
		String email = body.getEmail();
		String password = body.getPassword();
		String accessToken = authenticationService.userSignUp(username, email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Login")
	@PostMapping("/login")
	public TokenDTO login(@Valid @RequestBody UserLoginDTO body) throws UserCredentialsIsInvalidException {
		String email = body.getEmail();
		String password = body.getPassword();

		String accessToken = authenticationService.loginUser(email, password);

		return new TokenDTO(accessToken);
	}

	@Operation(summary = "Verify email")
	@PostMapping("/email/verify")
	public OkDTO emailVerify(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody EmailCodeDTO body) throws CodeIsInvalidException, CodeExpiredException {
		authenticationService.verifyEmail(user, body.getCode());

		return new OkDTO(true);
	}

	@Operation(summary = "Resend email")
	@PostMapping("/email/resend")
	public OkDTO resendEmail(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.ACCESS_TOKEN) String accessToken) throws CodeIsInvalidException, NeedToWaitBeforeResendException {
		authenticationService.resendEmail(user, accessToken);

		return new OkDTO(true);
	}
}
