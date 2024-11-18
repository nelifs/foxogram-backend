package su.foxogram.constants;

public class CodesConstants {
	public enum Lifetime {
		DELETE(900000),
		VERIFY(600000);

		private final long time;

		Lifetime(long time) {
			this.time = time;
		}

		public long getValue() {
			return time;
		}
	}
}
