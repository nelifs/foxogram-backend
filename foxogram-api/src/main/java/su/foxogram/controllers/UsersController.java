package su.foxogram.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.foxogram.constructors.User;
import su.foxogram.enums.APIEnum;

import java.util.List;

@RestController
@RequestMapping(value = APIEnum.USERS, produces = "application/json")
public class UsersController {

	@GetMapping
	public List<User> getUsers() {

		return null;
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable long id) {

		return null;
	}
}
