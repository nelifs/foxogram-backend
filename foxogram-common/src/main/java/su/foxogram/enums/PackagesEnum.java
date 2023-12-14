package su.foxogram.enums;

import static su.foxogram.Main.dotenv;

public class PackagesEnum {
    public enum Packages {
        ENDPOINTS("endpoints"),
        EVENTS("EVENTS");

        private final String packageName;

        Packages(final String packageName) {
            this.packageName = packageName;
        }

        public String getValue() {
            return packageName;
        }
    }
}
