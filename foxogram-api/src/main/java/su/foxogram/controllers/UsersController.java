package su.foxogram.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.response.UserDTO;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.dtos.response.UserMeDTO;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.exceptions.UserCredentialsDuplicateException;
import su.foxogram.models.User;
import su.foxogram.constants.APIConstants;
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
	public UserMeDTO getYourself(@RequestAttribute(value = "user") User user) {
		return new UserMeDTO(user);
	}

	@GetMapping("/{key}")
	public UserDTO getUser(@PathVariable String key) throws UserNotFoundException {
		User user = usersService.getUser(key);

		return new UserDTO(user);
	}

	@PatchMapping("/@me")
	public UserDTO editUser(@RequestAttribute(value = "user") User user, @Valid @RequestBody UserEditDTO body) throws UserCredentialsDuplicateException {
		user = usersService.editUser(user, body);

		return new UserDTO(user);
	}
}
