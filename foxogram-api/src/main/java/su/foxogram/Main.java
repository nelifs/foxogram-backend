package su.foxogram;

import su.foxogram.constructors.WebServer;
import su.foxogram.enums.APIEnum;
import su.foxogram.interfaces.Route;
import su.foxogram.routes.auth.AuthRoute;
import su.foxogram.routes.data.DataRoute;

public class Main {
    public static void main(String[] args) {
        new WebServer()
                .addRoute(APIEnum.Routes.DATA, DataRoute.router)
                .addRoute(APIEnum.Routes.AUTH, AuthRoute.router)
                .setPort(8080)
                .start();

            Route routeAnnotation = AuthRoute.class.getAnnotation(Route.class);
    }
}