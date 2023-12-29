package su.foxogram.constructors;

import su.foxogram.util.Converter;

import java.util.HashMap;

public class RequestMessage {
    public HashMap<String, String> hashMap;
    public boolean success;

    public RequestMessage() {
        hashMap = new HashMap<>();
    }

    public RequestMessage setSuccess(boolean value) {
        hashMap.put("success", String.valueOf(value));

        return this;
    }

    public RequestMessage addField(String key, String value) {
        hashMap.put(key, value);

        return this;
    }

    public String build() {
        return (String) Converter.hashMapToJSON(hashMap);
    }
}
