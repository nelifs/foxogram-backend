package su.foxogram.enums;

public class ExceptionsEnum {
	public enum Codes {
		MESSAGE_NOT_FOUND(101),
		CHANNEL_NOT_FOUND(102),
		USER_NOT_FOUND(201),
		USER_EMAIL_NOT_VERIFIED(202),
		USER_AUTHENTICATION_NEEDED(203),
		MEMBER_IN_CHANNEL_NOT_FOUND(301);

		private final int code;

		Codes(int code) {
			this.code = code;
		}

		public int getValue() {
			return code;
		}
	}
}
