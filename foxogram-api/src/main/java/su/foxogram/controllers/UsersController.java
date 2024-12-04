package su.foxogram.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.dtos.response.UserDTO;
import su.foxogram.exceptions.UserCredentialsDuplicateException;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.models.User;
import su.foxogram.services.UsersService;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {

	private final UsersService usersService;

	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	@GetMapping("/@me")
	public UserDTO getYourself(@RequestAttribute(value = "user") User authenticatedUser) {
		return new UserDTO(authenticatedUser, true);
	}

	@GetMapping("/{userKey}")
	public UserDTO getUser(@PathVariable String userKey) throws UserNotFoundException {
		User fetchedUser = usersService.getUser(userKey);

		return new UserDTO(fetchedUser, false);
	}

	@PatchMapping("/@me")
	public UserDTO editUser(@RequestAttribute(value = "user") User authenticatedUser, @Valid @RequestBody UserEditDTO userEditRequest) throws UserCredentialsDuplicateException {
		authenticatedUser = usersService.editUser(authenticatedUser, userEditRequest);

		return new UserDTO(authenticatedUser, false);
	}
}
