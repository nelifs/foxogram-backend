package su.foxogram.constants;

public class ChannelsConstants {
	public enum Type {
		GROUP("group"),
		CHANNEL("channel"),
		DM("dm");

		private final String type;

		Type(String type) {
			this.type = type;
		}

		public String getValue() {
			return type;
		}
	}
}
