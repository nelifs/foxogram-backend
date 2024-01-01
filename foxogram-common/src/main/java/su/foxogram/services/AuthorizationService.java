package su.foxogram.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.RequestMessage;
import su.foxogram.constructors.Session;
import su.foxogram.constructors.User;
import su.foxogram.exceptions.UserAuthenticationNeededException;
import su.foxogram.exceptions.UserEmailNotVerifiedException;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.repositories.SessionRepository;
import su.foxogram.repositories.UserRepository;

@Service
public class AuthorizationService {

	private final UserRepository userRepository;
	private final SessionRepository sessionRepository;

	@Autowired
	public AuthorizationService(UserRepository userRepository, SessionRepository sessionRepository) {
		this.userRepository = userRepository;
		this.sessionRepository = sessionRepository;
	}

	public String getToken(HttpServletRequest request) {
		String headerValue = request.getHeader("Authorization");
		if (headerValue == null) return null;
		else return headerValue.substring(7);
	}

	public User getUser(HttpServletRequest request, boolean validateSession, boolean validateEmail) throws UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		return validate(getToken(request), validateSession, validateEmail);
	}

	public User validate(String token, boolean validateSession, boolean validateEmail) throws UserEmailNotVerifiedException, UserNotFoundException, UserAuthenticationNeededException {
		User user = userRepository.findByAccessToken(token);

		if (user == null) {
			throw new UserNotFoundException();
		}

		if (!user.isEmailVerified() && validateEmail) {
			throw new UserEmailNotVerifiedException();
		}

		Session session = sessionRepository.findById(user.getId());

		if (session == null && validateSession) {
			throw new UserAuthenticationNeededException();
		}

		return userRepository.findByAccessToken(token);
	}

	public ResponseEntity<String> handleNotVerifiedEmail() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RequestMessage().setSuccess(false).addField("message", "You need to verify your email first").build());
	}
}
