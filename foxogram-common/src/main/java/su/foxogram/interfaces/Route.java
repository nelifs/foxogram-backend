package su.foxogram.interfaces;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public interface Route {
    Router router = Router.router(Vertx.vertx());
    default Router router() {
        router.route().handler(CorsHandler.create("*"));
        router.route().handler(BodyHandler.create());

        router.post().handler(this::handlePost);
        router.get().handler(this::handleGet);
        router.patch().handler(this::handlePatch);
        router.delete().handler(this::handleDelete);

        return router;
    }

    void handlePost(RoutingContext context);

    void handleGet(RoutingContext context);

    void handlePatch(RoutingContext context);

    void handleDelete(RoutingContext context);
}
