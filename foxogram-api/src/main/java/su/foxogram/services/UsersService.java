package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.User;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.repositories.UserRepository;

@Service
public class UsersService {

	private final UserRepository userRepository;
	private final AuthenticationService authenticationService;
	private final CassandraTemplate cassandraTemplate;

	@Autowired
	public UsersService(UserRepository userRepository, AuthenticationService authenticationService, CassandraTemplate cassandraTemplate) {
		this.userRepository = userRepository;
		this.authenticationService = authenticationService;
		this.cassandraTemplate = cassandraTemplate;
	}

	public User getUser(long id) throws UserNotFoundException {
		User user = userRepository.findById(id);

		if (user == null) {
			throw new UserNotFoundException();
		}

		return user;
	}

	public User editUser(long id) throws UserNotFoundException {
		return null;
	}
}
