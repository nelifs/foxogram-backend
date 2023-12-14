package su.foxogram.routes.auth;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import su.foxogram.structures.Handler;
import su.foxogram.constructors.Router;
import su.foxogram.constructors.Headers;
import su.foxogram.enums.APIEnum;
import su.foxogram.enums.PackagesEnum;
import su.foxogram.interfaces.Route;
import su.foxogram.util.Converter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@Route(path = APIEnum.Routes.AUTH)
public class AuthRoute {
    public static io.vertx.ext.web.Router router;

    public static void main(String[] args) {
        new Router(router).addHandler(AuthRoute::handlePost);
        Handler.initClasses(PackagesEnum.Packages.ENDPOINTS);
    }

    private static void handlePost(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpServerRequest request = context.request();
        Headers.set(response);

        String body = String.valueOf(context.getBodyAsJson());
        HashMap<String, String> hashMapBody = Converter.JSONToHashMap(body);
        String uri = request.uri();

        try {
            Handler.startHandlingEndpoints(uri, request, response, hashMapBody);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
