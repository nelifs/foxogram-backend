package su.foxogram.util;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import su.foxogram.exceptions.MFAIsInvalidException;
import su.foxogram.exceptions.TOTPKeyIsInvalidException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class Totp {
	private static final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

	public static Key generateKey() throws NoSuchAlgorithmException {
		final KeyGenerator keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());

		final int macLengthInBytes = Mac.getInstance(totp.getAlgorithm()).getMacLength();
		keyGenerator.init(macLengthInBytes * 8);

		return keyGenerator.generateKey();
	}

	public static boolean validate(String userKey, String userOTP) throws TOTPKeyIsInvalidException, MFAIsInvalidException {
		final Instant now = Instant.now();
		String serverOTP;
		Key key = new SecretKeySpec(userKey.getBytes(), 0, userKey.getBytes().length, "DES");

		try {
			serverOTP = totp.generateOneTimePasswordString(key, now);
		} catch (InvalidKeyException e) {
			throw new TOTPKeyIsInvalidException();
		}

		boolean MFAVerified = userOTP.equals(serverOTP);

		if (!MFAVerified) throw new MFAIsInvalidException();

		return true;
	}
}
