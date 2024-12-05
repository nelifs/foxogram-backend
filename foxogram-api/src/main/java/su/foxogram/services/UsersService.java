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
import su.foxogram.models.Code;
import su.foxogram.models.User;
import su.foxogram.repositories.CodeRepository;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;

import java.util.Optional;

@Slf4j
@Service
public class UsersService {

	private final UserRepository userRepository;

	private final EmailService emailService;

	private final CodeRepository codeRepository;

	@Autowired
	public UsersService(UserRepository userRepository, EmailService emailService, CodeRepository codeRepository) {
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.codeRepository = codeRepository;
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

	public void requestUserDelete(User user, String password, String accessToken) throws UserCredentialsIsInvalidException, CodeIsInvalidException {
		if (!Encryptor.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		sendEmail(user, EmailConstants.Type.ACCOUNT_DELETE);
	}

	public void confirmUserDelete(User user, String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = validateCode(pathCode);

		deleteUserAndCode(user, code);
	}

	private Code validateCode(String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeRepository.findByValue(pathCode);

		if (code == null)
			throw new CodeIsInvalidException();

		if (code.expiresAt <= System.currentTimeMillis())
			throw new CodeExpiredException();

		return code;
	}

	private void deleteUserAndCode(User user, Code code) {
		deleteUser(user);
		deleteVerificationCode(code);
	}

	private void deleteVerificationCode(Code code) {
		codeRepository.delete(code);
		log.info("CODE record deleted ({}, {}) successfully", code.getUserId(), code.getValue());
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
