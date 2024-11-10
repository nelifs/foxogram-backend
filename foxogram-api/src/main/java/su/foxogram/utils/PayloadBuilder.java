package su.foxogram.utils;

import su.foxogram.util.Converter;

import java.util.HashMap;

public class PayloadBuilder {
    public final HashMap<String, String> hashMap;
    public boolean success;

    public PayloadBuilder() {
        hashMap = new HashMap<>();
    }

    public PayloadBuilder setSuccess(boolean value) {
        hashMap.put("success", String.valueOf(value));

        return this;
    }

    public PayloadBuilder addField(String key, String value) {
        hashMap.put(key, value);

        return this;
    }

    public String build() {
        return (String) Converter.hashMapToJSON(hashMap);
    }
}
