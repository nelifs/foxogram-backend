package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.models.User;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.repositories.UserRepository;

@Service
public class UsersService {

	private final UserRepository userRepository;

    @Autowired
	public UsersService(UserRepository userRepository) {
		this.userRepository = userRepository;
    }

	public User getUser(String id, User user) throws UserUnauthorizedException {
		if (!id.equals("@me")) user = userRepository.findById(Long.parseLong(id));

		if (user == null) {
			throw new UserUnauthorizedException();
		}

		return user;
	}

	public User editUser(User user) {
		return null;
	}
}
