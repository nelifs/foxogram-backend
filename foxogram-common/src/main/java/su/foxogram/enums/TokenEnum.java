package su.foxogram.enums;

public class TokenEnum {
	public enum Lifetime {
		ACCESS_TOKEN(604800000),
		REFRESH_TOKEN(2628000000L),
		RESUME_TOKEN(604800000);

		private final long time;

		Lifetime(long time) {
			this.time = time;
		}

		public long getValue() {
			return time;
		}
	}

	public enum Type {
		ACCESS_TOKEN("access_token"),
		REFRESH_TOKEN("refresh_token"),
		RESUME_TOKEN("resume_token");

		private final String type;

		Type(String type) {
			this.type = type;
		}

		public String getValue() {
			return type;
		}
	}
}
