package su.foxogram.constructors;

import io.vertx.core.http.HttpServerResponse;

public class Headers {
    public static void set(HttpServerResponse response) {
        response.putHeader("Access-Control-Allow-Origin", "*");
        response.putHeader("Access-Control-Allow-Headers", "*");
        response.putHeader("Access-Control-Allow-Methods", "*");
        response.putHeader("Content-Type", "application/json");
    }
}
