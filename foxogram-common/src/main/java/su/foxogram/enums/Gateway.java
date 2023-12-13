package su.foxogram.enums;

public class Gateway {
    public enum Events {
        MESSAGE_CREATE("01"),
        MESSAGE_UPDATE("02"),
        MESSAGE_DELETE("03"),
        USER_UPDATE("04"),
        HELLO("05"),
        HEARTBEAT("06"),
        READY("07");

        private final String event;

        Events(final String event) {
            this.event = event;
        }

        @Override
        public String toString() {
            return event;
        }
    }
}
