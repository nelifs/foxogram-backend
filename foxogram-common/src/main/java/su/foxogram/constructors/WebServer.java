package su.foxogram.constructors;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import su.foxogram.enums.APIEnum;

import java.util.concurrent.TimeUnit;

public class WebServer {
    public static final VertxOptions options = new VertxOptions();
    public static final Vertx vertx = Vertx.vertx(options);
    public static final Router router = Router.router(vertx);
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
                System.out.println("Server started on port " + PORT);
            } else {
                System.out.println("Failed to start server: " + ar.cause().getMessage());
            }
        });
    }

    public WebServer addRoute(APIEnum.Routes route, Router router) {
        router.mountSubRouter(route.getValue(), router);
        return this;
    }

    public WebServer setPort(int port) {
        PORT = port;
        return this;
    }
}
