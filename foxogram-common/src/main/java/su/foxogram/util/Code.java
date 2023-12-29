package su.foxogram.util;

import java.util.Random;

public class Code {
	public static String generateDigitCode() {
		Random random = new Random();
		int min = 100000;
		int max = 999999;

		int generatedNumber = random.nextInt((max - min) + 1) + min;
		return String.format("%06d", generatedNumber);
	}

	public static String generateLetterCode() {
		int codeLength = 10;
		StringBuilder code = new StringBuilder();

		Random random = new Random();

		for (int i = 0; i < codeLength; i++) {
			char randomChar = (char) (random.nextInt(26) + 'A');
			code.append(randomChar);
		}

		return code.toString();
	}
}
