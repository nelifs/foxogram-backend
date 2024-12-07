package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.request.UserDeleteDTO;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.dtos.response.MFAKeyDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.dtos.response.UserDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.User;
import su.foxogram.services.MfaService;
import su.foxogram.services.UsersService;

import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "Users")
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {
	private final UsersService usersService;

	private final MfaService mfaService;

	public UsersController(UsersService usersService, MfaService mfaService) {
		this.usersService = usersService;
		this.mfaService = mfaService;
	}

	@Operation(summary = "Get user")
	@GetMapping("/{userKey}")
	public UserDTO getUser(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @PathVariable String userKey) throws UserNotFoundException {
		if (Objects.equals(userKey, "@me")) {
			return new UserDTO(authenticatedUser, true);
		}

		return new UserDTO(usersService.getUser(userKey), false);
	}

	@Operation(summary = "Edit user")
	@PatchMapping("/@me")
	public UserDTO editUser(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @Valid @RequestBody UserEditDTO userEditRequest) throws UserCredentialsDuplicateException {
		authenticatedUser = usersService.editUser(authenticatedUser, userEditRequest);

		return new UserDTO(authenticatedUser, false);
	}

	@Operation(summary = "Delete")
	@DeleteMapping("/@me")
	public OkDTO deleteUser(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody UserDeleteDTO body) throws UserCredentialsIsInvalidException {
		String password = body.getPassword();
		log.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		usersService.requestUserDelete(user, password);

		return new OkDTO(true);
	}

	@Operation(summary = "Confirm delete")
	@DeleteMapping("/@me/confirm")
	public OkDTO deleteUserConfirm(@RequestAttribute(value = AttributesConstants.USER) User user) {
		log.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		usersService.confirmUserDelete(user);

		return new OkDTO(true);
	}

	@Operation(summary = "Setup MFA")
	@PutMapping("/@me/mfa")
	public MFAKeyDTO setupMFA(@RequestAttribute(value = AttributesConstants.USER) User user) throws MFAIsAlreadySetException {
		log.info("USER mfa setup ({}, {}) request", user.getId(), user.getEmail());

		String key = mfaService.setupMFA(user);

		return new MFAKeyDTO(key);
	}

	@Operation(summary = "Validate MFA")
	@PostMapping("/@me/mfa")
	public OkDTO validateMfa(@RequestAttribute(value = AttributesConstants.USER) User user) {
		log.info("USER mfa validation ({}, {}) request", user.getId(), user.getEmail());

		return new OkDTO(true);
	}

	@Operation(summary = "Delete MFA")
	@DeleteMapping("/@me/mfa")
	public OkDTO deleteMFA(@RequestAttribute(value = AttributesConstants.USER) User user) throws MFAIsNotSetException {
		log.info("USER mfa delete ({}, {}) request", user.getId(), user.getEmail());

		mfaService.deleteMFA(user);

		return new OkDTO(true);
	}
}
