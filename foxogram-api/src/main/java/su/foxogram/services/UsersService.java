package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import su.foxogram.models.User;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.repositories.cassandra.UserRepository;

@Service
public class UsersService {

	private final UserRepository userRepository;

    @Autowired
	public UsersService(UserRepository userRepository, AuthenticationService authenticationService, CassandraTemplate cassandraTemplate) {
		this.userRepository = userRepository;
    }

	public User getUser(long id) throws UserNotFoundException {
		User user = userRepository.findById(id);

		if (user == null) {
			throw new UserNotFoundException();
		}

		return user;
	}

	public User editUser(long id) {
		return null;
	}
}
