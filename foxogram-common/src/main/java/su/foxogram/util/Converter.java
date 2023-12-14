package su.foxogram.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Converter {
    public static HashMap<String, String> JSONToHashMap(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();

        return gson.fromJson(jsonString, type);
    }

    public static Object hashMapToJSON(HashMap<String, String> hashMap) {
        Gson gson = new Gson();
        return gson.toJson(hashMap);
    }
}
