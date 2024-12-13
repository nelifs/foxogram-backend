package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constants.BucketsConstants;
import su.foxogram.constants.CodesConstants;
import su.foxogram.constants.EmailConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.Code;
import su.foxogram.models.User;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.CodeGenerator;
import su.foxogram.util.Encryptor;

@Slf4j
@Service
public class UsersService {
	private final UserRepository userRepository;

	private final EmailService emailService;

	private final CodeService codeService;

	private final StorageService storageService;

	@Autowired
	public UsersService(UserRepository userRepository, EmailService emailService, CodeService codeService, StorageService storageService) {
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.codeService = codeService;
		this.storageService = storageService;
	}

	public User getUser(String username) throws UserNotFoundException {
		User user = userRepository.findByUsername(username);

		if (user == null) throw new UserNotFoundException();

		return user;
	}

	public User editUser(User user, UserEditDTO body) throws UserCredentialsDuplicateException, UploadFailedException {
		if (body.getDisplayName() != null) user.setDisplayName(body.getDisplayName());
		if (body.getAvatar() != null) changeAvatar(user, body);

		try {
			if (body.getUsername() != null) user.setUsername(body.getUsername());
			if (body.getEmail() != null) changeEmail(user, body);
			if (body.getPassword() != null) changePassword(user, body);

			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		return user;
	}

	public void requestUserDelete(User user, String password) throws UserCredentialsIsInvalidException {
		if (!Encryptor.verifyPassword(password, user.getPassword()))
			throw new UserCredentialsIsInvalidException();

		sendEmail(user, EmailConstants.Type.ACCOUNT_DELETE);
	}

	public void confirmUserDelete(User user, String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeService.validateCode(pathCode);

		userRepository.delete(user);
		log.info("User deleted ({}, {}) successfully", user.getId(), user.getEmail());

		codeService.deleteCode(code);
	}

	private void changeAvatar(User user, UserEditDTO body) throws UploadFailedException {
		String hash;

		try {
			hash = storageService.uploadFile(body.getAvatar(), BucketsConstants.AVATARS_BUCKET);
		} catch (Exception e) {
			throw new UploadFailedException();
		}

		user.setAvatar(hash);
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
