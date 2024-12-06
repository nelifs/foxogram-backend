package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constants.CodesConstants;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.User;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;
import su.foxogram.util.Totp;

import java.util.Optional;

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
		Optional<User> optionalUser = userRepository.findByIdOrUsername(key, key);

		if (optionalUser.isEmpty()) throw new UserNotFoundException();

		return optionalUser.get();
	}

	public User editUser(User user, UserEditDTO body) throws UserCredentialsDuplicateException {
		if (body.getDisplayName() != null) user.setDisplayName(body.getDisplayName());
		if (body.getAvatar() != null) user.setAvatar(body.getAvatar());

		try {
			if (body.getUsername() != null) user.setUsername(body.getUsername());
			if (body.getEmail() != null) changeEmail(user, body);
			if (body.getPassword() != null) changePassword(user, body);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		userRepository.save(user);

		return user;
	}

	public void requestUserDelete(User user, String password) throws UserCredentialsIsInvalidException {
		if (!Encryptor.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		sendEmail(user, EmailConstants.Type.ACCOUNT_DELETE);
	}

	public void confirmUserDelete(User user) {
		deleteUser(user);
	}

	public String setupMFA(User user) throws MFAIsAlreadySetException {
		if (user.hasFlag(UserConstants.Flags.MFA_ENABLED)) throw new MFAIsAlreadySetException();

		String secretKey = Totp.generateKey();

		user.addFlag(UserConstants.Flags.MFA_ENABLED);
		user.addFlag(UserConstants.Flags.AWAITING_CONFIRMATION);
		user.setKey(secretKey);

		userRepository.save(user);

		return secretKey;
	}

	public void deleteMFA(User user) throws MFAIsNotSetException {
		if (!user.hasFlag(UserConstants.Flags.MFA_ENABLED)) throw new MFAIsNotSetException();

		user.removeFlag(UserConstants.Flags.MFA_ENABLED);
		user.setKey(null);
	}

	private void deleteUser(User user) {
		userRepository.delete(user);
		log.info("USER record deleted ({}, {}) successfully", user.getId(), user.getEmail());
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
