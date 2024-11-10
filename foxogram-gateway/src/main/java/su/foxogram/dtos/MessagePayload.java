package su.foxogram.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessagePayload {
    @JsonProperty("code")
    int code;
    @JsonProperty("name")
    String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
