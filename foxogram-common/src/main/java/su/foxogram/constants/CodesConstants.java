package su.foxogram.constants;

public class CodesConstants {
	public enum Lifetime {
		DELETE(3600000),
		VERIFY(3600000),
		RESEND(60000);

		private final long time;

		Lifetime(long time) {
			this.time = time;
		}

		public long getValue() {
			return time;
		}
	}
}
