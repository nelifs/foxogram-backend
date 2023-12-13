package su.foxogram.enums;

public class API {
    public enum Routes {
        DATA("/data"),
        AUTH("/auth");

        private final String route;

        Routes(final String route) {
            this.route = route;
        }

        @Override
        public String toString() {
            return route;
        }
    }

    public enum Endpoints {
        MESSAGES(Routes.DATA.route + "/messages"),  
        USERS(Routes.DATA.route + "/users"),
        ATTACHMENTS(Routes.DATA.route + "/attachments"),
        AVATARS(Routes.DATA.route + "/avatars");

        private final String endpoint;

        Endpoints(final String endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public String toString() {
            return endpoint;
        }
    }
}
