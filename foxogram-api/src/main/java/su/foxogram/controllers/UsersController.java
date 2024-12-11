package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.request.CodeDTO;
import su.foxogram.dtos.request.UserDeleteDTO;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.dtos.response.UserDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.User;
import su.foxogram.services.UsersService;

import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "Users")
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {
	private final UsersService usersService;

	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	@Operation(summary = "Get user")
	@GetMapping("/{username}")
	public UserDTO getUser(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @PathVariable String username) throws UserNotFoundException {
		if (Objects.equals(username, "@me")) {
			return new UserDTO(authenticatedUser, true);
		}

		return new UserDTO(usersService.getUser(username), false);
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
	@PostMapping("/@me/confirm")
	public OkDTO deleteUserConfirm(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestBody CodeDTO body) throws CodeExpiredException, CodeIsInvalidException {
		log.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		usersService.confirmUserDelete(user, body.getCode());

		return new OkDTO(true);
	}
}
