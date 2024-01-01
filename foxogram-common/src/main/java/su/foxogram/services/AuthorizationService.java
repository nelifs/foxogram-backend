package su.foxogram.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.User;
import su.foxogram.repositories.UserRepository;

@Service
public class AuthorizationService {

	private final UserRepository userRepository;

	@Autowired
	public AuthorizationService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String getToken(HttpServletRequest request) {
		return request.getHeader("Authorization").substring(7);
	}

	public User getUser(HttpServletRequest request) {
		return validate(getToken(request));
	}

	public User validate(String token) {
		return userRepository.findByAccessToken(token);
	}
}
