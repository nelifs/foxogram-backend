package su.foxogram.structures;

import org.springframework.beans.factory.annotation.Autowired;
import su.foxogram.constructors.User;
import su.foxogram.repositories.UserRepository;

public class AuthorizationController {
	@Autowired
	static UserRepository userRepository;

	public static User getUser(String authorization) {
		String token = authorization.substring(7);

		return userRepository.findByAccessToken(token);
	}
}
