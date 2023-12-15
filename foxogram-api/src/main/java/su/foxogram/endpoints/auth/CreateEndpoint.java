package su.foxogram.endpoints.auth;


import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import su.foxogram.enums.APIEnum;
import su.foxogram.interfaces.Endpoint;

import java.io.IOException;
import java.util.HashMap;

@Endpoint(path = APIEnum.Endpoints.CREATE)
public class CreateEndpoint {

    public static void handle(HttpServerRequest request, HttpServerResponse response, HashMap<String, String> data) throws IOException {
        HttpMethod method = request.method();

        if (method == HttpMethod.POST) {

        }
    }
}
