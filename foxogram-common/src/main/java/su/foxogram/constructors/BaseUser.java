package su.foxogram.constructors;

import java.util.List;

public class BaseUser {
    public long id;
    public String username, accessToken, avatar;
    public List<String> flags;
    public long createdAt;

    public BaseUser() {

    }

    public BaseUser(long id, String avatar, String username, String accessToken, long createdAt, List<String> flags) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.createdAt = createdAt;
        this.flags = flags;
        this.accessToken = accessToken;
    }
}
