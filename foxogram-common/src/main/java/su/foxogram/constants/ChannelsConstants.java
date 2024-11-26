package su.foxogram.constants;

import lombok.Getter;

public class ChannelsConstants {
	@Getter
	public enum Type {
		DM(1),
		GROUP(2),
		CHANNEL(3);

		private final int type;

		Type(int type) {
			this.type = type;
		}
	}
}
