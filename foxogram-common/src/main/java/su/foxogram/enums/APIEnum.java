package su.foxogram.enums;

import static su.foxogram.Main.dotenv;

public class APIEnum {
    public enum Routes {
        DATA("v" + dotenv.get("API_VERSION") + "/data"),
        AUTH("v" + dotenv.get("API_VERSION") + "/auth");

        private final String route;

        Routes(final String route) {
            this.route = route;
        }

        public String getValue() {
            return route;
        }
    }

    public enum Endpoints {
        LOGIN(Routes.AUTH.route + "/login"),
        CREATE(Routes.AUTH.route + "/create"),
        MESSAGES(Routes.DATA.route + "/messages"),  
        USERS(Routes.DATA.route + "/users"),
        ATTACHMENTS(Routes.DATA.route + "/attachments"),
        AVATARS(Routes.DATA.route + "/avatars");

        private final String endpoint;

        Endpoints(final String endpoint) {
            this.endpoint = endpoint;
        }

        public String getValue() {
            return endpoint;
        }
    }
}
