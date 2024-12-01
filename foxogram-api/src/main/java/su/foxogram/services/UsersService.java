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
import su.foxogram.structures.Snowflake;
import su.foxogram.util.CodeGenerator;

import java.util.Arrays;

import static su.foxogram.structures.Snowflake.DEFAULT_CUSTOM_EPOCH;

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

	public User getUser(String key) throws UserNotFoundException {
		User user = userRepository.findByUsername(key);
		if (user == null) user = userRepository.findById(Long.parseLong(key));

		if (user == null) throw new UserNotFoundException();

		return user;
	}

	public User editUser(User user, UserEditDTO body) throws UserWithThisUsernameOrEmailAlreadyExistException {
		if (body.getDisplayName() != null) user.setDisplayName(body.getDisplayName());
		if (body.getAvatar() != null) user.setAvatar(body.getAvatar());

		try {
			if (body.getUsername() != null) user.setUsername(body.getUsername());
			if (body.getEmail() != null) changeEmail(user, body);
			if (body.getPassword() != null) changePassword(user, body);
		} catch (DataIntegrityViolationException e) {
			throw new UserWithThisUsernameOrEmailAlreadyExistException();
		}

		userRepository.save(user);

		return user;
	}

	private void changeEmail(User user, UserEditDTO body) {
		user.setEmail(body.getEmail());
		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstants.Type.EMAIL_VERIFY);
	}

	private void changePassword(User user, UserEditDTO body) {
		user.setPassword(body.getPassword());
		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);

		sendEmail(user, EmailConstants.Type.RESET_PASSWORD);
	}

	private void sendEmail(User user, EmailConstants.Type type) {
		String emailType = type.getValue();
		String code = CodeGenerator.generateDigitCode();
		long issuedAt = System.currentTimeMillis();
		long expiresAt = issuedAt + CodesConstants.Lifetime.BASE.getValue();

		emailService.sendEmail(user.getEmail(), user.getId(), emailType, user.getUsername(), code, issuedAt, expiresAt, null);
	}
}
