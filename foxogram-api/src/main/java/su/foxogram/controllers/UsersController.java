package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.response.UserDTO;
import su.foxogram.models.User;
import su.foxogram.constants.APIConstants;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.services.UsersService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {

	private final UsersService usersService;

	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	@GetMapping
	public List<User> getUsers() {

		return null;
	}

	@GetMapping("/{id}")
	public UserDTO getUser(@RequestAttribute(value = "user") User user, @PathVariable String id, HttpServletRequest request) throws UserUnauthorizedException {
		user = usersService.getUser(id, user);

		return new UserDTO(user);
	}

	@PatchMapping("/@me")
	public User editUser(@RequestAttribute(value = "user") User user) {

		return usersService.editUser(user);
	}
}
