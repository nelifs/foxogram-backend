package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.models.User;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.repositories.UserRepository;

@Slf4j
@Service
public class UsersService {

	private final UserRepository userRepository;

    @Autowired
	public UsersService(UserRepository userRepository) {
		this.userRepository = userRepository;
    }

	public User getUser(String id, User user) throws UserUnauthorizedException, UserNotFoundException {
		if (!id.equals("@me")) user = userRepository.findById(Long.parseLong(id));
		else if (user == null) throw new UserUnauthorizedException();

		if (user == null) throw new UserNotFoundException();

		return user;
	}

	public User editUser(User user) {
		return null;
	}
}
