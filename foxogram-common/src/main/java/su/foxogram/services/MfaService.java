package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constants.UserConstants;
import su.foxogram.exceptions.MFAIsAlreadySetException;
import su.foxogram.exceptions.MFAIsNotSetException;
import su.foxogram.models.User;
import su.foxogram.repositories.UserRepository;
import su.foxogram.util.Totp;

@Service
public class MfaService {
	private final UserRepository userRepository;

	@Autowired
	public MfaService(UserRepository userRepository) {
		this.userRepository = userRepository;
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
}
