package su.foxogram.enums;

public class GatewayEnum {
    public enum Events {
        MESSAGE_CREATE(101),
        MESSAGE_UPDATE(102),
        MESSAGE_DELETE(103),
        USER_UPDATE(201),
        HELLO(301),
        HEARTBEAT(302),
        READY(304),
        OTTER(1);

        private final int event;

        Events(int event) {
            this.event = event;
        }

        public int getValue() {
            return event;
        }
    }
}
