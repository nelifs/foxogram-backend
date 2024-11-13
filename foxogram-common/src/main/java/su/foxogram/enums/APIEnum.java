package su.foxogram.enums;

import lombok.Getter;

public class APIEnum {
    public static final String MESSAGES = "/v1/messages";
    public static final String USERS = "/v1/users";
    public static final String AUTH = "/v1/auth";
    public static final String CHANNELS = "/v1/channels";

    public static final String PROMETHEUS_BASE = "api.requests.";

    @Getter
    public enum RateLimit {
        CAPACITY(20),
        TOKENS(20),
        MINUTES(1);

        private final int value;

        RateLimit(int value) {
            this.value = value;
        }

    }

    @Getter
    public enum Prometheus {
        AUTH(),
        CHANNELS(),
        EXCEPTIONS(),
        MESSAGES(),
        USERS();

        private final String value;

        Prometheus() {
            this.value = "api.requests.auth.";
        }

    }


}
