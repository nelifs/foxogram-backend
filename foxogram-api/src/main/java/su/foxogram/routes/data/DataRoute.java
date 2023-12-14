package su.foxogram.routes.data;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import su.foxogram.constructors.Headers;
import su.foxogram.constructors.Router;
import su.foxogram.enums.APIEnum;
import su.foxogram.interfaces.Route;

@Route(path = APIEnum.Routes.DATA)
public class DataRoute {
    public static io.vertx.ext.web.Router router;

    public static void main(String[] args) {
        new Router(router).addHandler(DataRoute::handlePost);
    }

    private static void handlePost(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpServerRequest request = context.request();
        Headers.set(response);

        String body = context.getBodyAsString();
        String uri = request.uri();
    }
}
