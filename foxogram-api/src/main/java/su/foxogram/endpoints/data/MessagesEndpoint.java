package su.foxogram.endpoints.data;


import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import su.foxogram.enums.APIEnum;
import su.foxogram.interfaces.Endpoint;

import java.io.IOException;
import java.util.HashMap;

@Endpoint(path = APIEnum.Endpoints.CREATE)
public class MessagesEndpoint {

    public static void handle(HttpServerRequest request, HttpServerResponse response, HashMap<String, String> data) throws IOException {
        HttpMethod method = request.method();

        switch (method.toString()) {
            case "POST": {

            }
            case "GET": {

            }
            case "DELETE": {

            }
            case "PATCH": {

            }
        }
    }
}
