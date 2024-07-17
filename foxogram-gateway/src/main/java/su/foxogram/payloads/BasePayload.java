package su.foxogram.payloads;

public class BasePayload {
    public int code;
    public String name;

    public BasePayload(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
