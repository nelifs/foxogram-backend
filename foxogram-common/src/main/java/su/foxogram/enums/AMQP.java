package su.foxogram.enums;

public class AMQP {
    public enum Events {
        AVATAR_UPLOAD(401),
        ATTACHMENT_UPLOAD(402);

        private final int event;

        Events(int event) {
            this.event = event;
        }

        public int getValue() {
            return event;
        }
    }
}
