package su.foxogram.util;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    public static final Dotenv dotenv = Dotenv.configure().load();

    public static String get(String property) {
        return dotenv.get(property);
    }
}
