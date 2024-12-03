package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.response.UserDTO;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.exceptions.UserWithThisUsernameOrEmailAlreadyExistException;
import su.foxogram.models.User;
import su.foxogram.constants.APIConstants;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.services.UsersService;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {

	private final UsersService usersService;

	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	@GetMapping("/{id}")
	public UserDTO getUser(@RequestAttribute(value = "user") User authenticatedUser, @PathVariable String userId, HttpServletRequest request) throws UserUnauthorizedException, UserNotFoundException {
		User requestedUser = usersService.getUser(userId, authenticatedUser);

		return new UserDTO(requestedUser);
	}

	@PatchMapping("/@me")
	public UserDTO editUser(@RequestAttribute(value = "user") User authenticatedUser, @Valid @RequestBody UserEditDTO requestBody) throws UserWithThisUsernameOrEmailAlreadyExistException {
		User updatedUser = usersService.editUser(authenticatedUser, requestBody);

		return new UserDTO(updatedUser);
	}
}
