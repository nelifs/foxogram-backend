package su.foxogram.constants;

import lombok.Getter;

public class UserConstants {
	@Getter
	public enum Flags {
		AWAITING_CONFIRMATION(1),
		MFA_ENABLED(1 << 1),
		EMAIL_VERIFIED(1 << 2),
		DISABLED(1 << 3);

		private final long bit;

		Flags(long bit) {
			this.bit = bit;
		}
	}

	@Getter
	public enum Type {
		USER(1),
		BOT(2);

		private final int type;

		Type(int type) {
			this.type = type;
		}
	}
}
