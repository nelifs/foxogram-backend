package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constants.CodesConstants;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.exceptions.UserWithThisUsernameOrEmailAlreadyExistException;
import su.foxogram.models.User;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.CodeGenerator;

@Slf4j
@Service
public class UsersService {

	private final UserRepository userRepository;
	private final EmailService emailService;

	@Autowired
	public UsersService(UserRepository userRepository, EmailService emailService) {
		this.userRepository = userRepository;
		this.emailService = emailService;
	}

	public User getUser(String id, User user) throws UserUnauthorizedException, UserNotFoundException {
		if (!id.equals("@me")) user = userRepository.findById(Long.parseLong(id));
		else if (user == null) throw new UserUnauthorizedException();

		if (user == null) throw new UserNotFoundException();

		return user;
	}

	public User editUser(User user, UserEditDTO body) throws UserWithThisUsernameOrEmailAlreadyExistException {
		if (body.getDisplayName() != null) user.setDisplayName(body.getDisplayName());
		if (body.getAvatar() != null) user.setAvatar(body.getAvatar());

		try {
			if (body.getUsername() != null) user.setUsername(body.getUsername());
			if (body.getEmail() != null) changeEmail(user, body);
		} catch (DataIntegrityViolationException e) {
			throw new UserWithThisUsernameOrEmailAlreadyExistException();
		}

		userRepository.save(user);

		return user;
	}

	private void changeEmail(User user, UserEditDTO body) {
		user.setEmail(body.getEmail());
		user.removeFlag(UserConstants.Flags.EMAIL_VERIFIED);

		sendEmail(user);
	}

	private void sendEmail(User user) {
		String emailType = EmailConstants.Type.CONFIRM.getValue();
		String code = CodeGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + CodesConstants.Lifetime.DELETE.getValue();

		emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), code, issuedAt, expiresAt, null);
	}
}
