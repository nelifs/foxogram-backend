package su.foxogram.constants;

public class EmailConstants {
	public enum Type {
		CONFIRM("confirm"),
		DELETE("delete");

		private final String type;

		Type(String type) {
			this.type = type;
		}

		public String getValue() {
			return type;
		}
	}
}
