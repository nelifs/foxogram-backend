package su.foxogram.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.RequestMessage;
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
		String headerValue = request.getHeader("Authorization");
		if (headerValue == null) return null;
		else return headerValue.substring(7);
	}

	public User getUser(HttpServletRequest request) {
		return validate(getToken(request));
	}

	public User validate(String token) {
		return userRepository.findByAccessToken(token);
	}

	public ResponseEntity<String> handleNotVerifiedEmail() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestMessage().setSuccess(false).addField("message", "You need to verify your email first").build());
	}

	public ResponseEntity<String> handleNotAuthorized() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestMessage().setSuccess(false).addField("message", "You need to authorize first!").build());
	}
}
