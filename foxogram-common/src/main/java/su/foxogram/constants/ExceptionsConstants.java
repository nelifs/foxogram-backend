package su.foxogram.constants;

public class ExceptionsConstants {
	public enum Codes {
		MESSAGE_NOT_FOUND(101),
		CHANNEL_NOT_FOUND(201),
		USER_NOT_FOUND(301),
		USER_EMAIL_NOT_VERIFIED(302),
		USER_AUTHENTICATION_NEEDED(303),
		USER_WITH_THIS_EMAIL_ALREADY_EXIST(304),
		USER_CREDENTIALS_IS_INVALID(305),
		MEMBER_IN_CHANNEL_NOT_FOUND(401),
		MEMBER_ALREADY_IN_CHANNEL(402),
		MISSING_PERMISSIONS(403),
		CODE_IS_INVALID(501),
		CODE_EXPIRED(502);

		private final int code;

		Codes(int code) {
			this.code = code;
		}

		public int getValue() {
			return code;
		}
	}
}
