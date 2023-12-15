package su.foxogram;

import su.foxogram.constructors.WebServer;
import su.foxogram.enums.APIEnum;

public class Main {
    public static void main(String[] args) {
        new WebServer()
                .setEndpointsHandler(APIEnum.Routes.AUTH_RAW)
                .setEndpointsHandler(APIEnum.Routes.DATA_RAW)
                .setPort(8080)
                .start();
    }
}