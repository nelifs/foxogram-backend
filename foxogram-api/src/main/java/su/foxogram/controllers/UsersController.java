package su.foxogram.controllers;

import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.User;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.services.UsersService;

import java.util.List;

@RestController
@RequestMapping(value = APIEnum.USERS, produces = "application/json")
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
	public User getUser(@PathVariable long id) throws UserNotFoundException {

		return usersService.getUser(id);
	}

	@PatchMapping("/{id}")
	public User editUser(@PathVariable long id) throws UserNotFoundException {

		return usersService.editUser(id);
	}
}
