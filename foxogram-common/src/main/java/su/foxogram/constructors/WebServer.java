package su.foxogram.constructors;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import su.foxogram.enums.APIEnum;
import su.foxogram.structures.Handler;
import su.foxogram.util.Converter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WebServer {
    private static final Logger LOGGER = LogManager.getLogger(Handler.class);
    public static final VertxOptions options = new VertxOptions();
    public static final Vertx vertx = Vertx.vertx(options);
    public static final io.vertx.ext.web.Router router = io.vertx.ext.web.Router.router(vertx);
    private static int PORT;

    public static void main(String[] args) {
        options.setMaxEventLoopExecuteTime(15000);
        options.setMaxWorkerExecuteTime(15000);
        options.setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS);

        router.route().handler(CorsHandler.create("*"));
    }

    public void start() {
        vertx.createHttpServer().requestHandler(router).listen(PORT, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Server started on port " + PORT);
            } else {
                LOGGER.error("Failed to start server: " + ar.cause().getMessage());
            }
        });
    }

    public WebServer setEndpointsHandler(APIEnum.Routes route) {
        Handler.initEndpointsForRoute(route);

        new Router(router).addHandler(HttpMethod.POST, WebServer::handlePost);
        new Router(router).addHandler(HttpMethod.GET, WebServer::handleGet);
        new Router(router).addHandler(HttpMethod.DELETE, WebServer::handleDelete);
        new Router(router).addHandler(HttpMethod.PATCH, WebServer::handlePatch);

        return this;
    }

    public WebServer setPort(int port) {
        PORT = port;
        return this;
    }

    private static void initMethodHandling(RoutingContext context) {
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

    private static void handlePost(RoutingContext context) {
        initMethodHandling(context);
    }

    private static void handleGet(RoutingContext context) {
        initMethodHandling(context);
    }

    private static void handlePatch(RoutingContext context) {
        initMethodHandling(context);
    }

    private static void handleDelete(RoutingContext context) {
        initMethodHandling(context);
    }
}
