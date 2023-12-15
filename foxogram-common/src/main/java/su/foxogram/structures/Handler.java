package su.foxogram.structures;

import com.google.protobuf.Api;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import su.foxogram.Main;
import su.foxogram.enums.APIEnum;
import su.foxogram.enums.PackagesEnum;
import su.foxogram.interfaces.Endpoint;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Handler {
    private static final Logger LOGGER = LogManager.getLogger(Handler.class);
    private static List<Class<?>> classes;

    public static void initEndpointsForRoute(APIEnum.Routes route) {
        String packageName = "su.foxogram.endpoints" + route;
        URL resourceURL = Main.class.getClassLoader().getResource(packageName.replace(".", "/"));
        classes = new ArrayList<>();
        assert resourceURL != null;
        for (String path : resourceURL.getPath().split("!/")) {
            if (path.contains("class") && !path.contains("$")) {
                try {
                    classes.add(Class.forName(path));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void startHandlingEndpoints(String servicePath, HttpServerRequest request, HttpServerResponse response, HashMap<String, String> data) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Class<?> clazz : classes) {
            Endpoint endpoint = clazz.getAnnotation(Endpoint.class);
            if (endpoint != null) {
                APIEnum.Endpoints path = endpoint.path();
                Object instance = clazz.getDeclaredConstructor().newInstance();

                if (path.getValue().equals(servicePath)) {
                    clazz.getMethod("handle", HttpServerRequest.class, HttpServerResponse.class, HashMap.class).invoke(instance, request, response, data);
                }
            }
        }
    }
}
