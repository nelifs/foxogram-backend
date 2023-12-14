package su.foxogram.constructors;

import java.util.List;

public class User {
    public String id;
    public Avatar avatar;
    public String username, email, password, accessToken;
    public long createdAt;
    public List<String> flags;
    public long deletion;
    public boolean disabled, mfaEnabled;

    public User(String id, Avatar avatar, String username, String email, String password, String accessToken, long createdAt, List<String> flags, long deletion, boolean disabled, boolean mfaEnabled) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.createdAt = createdAt;
        this.flags = flags;
        this.email = email;
        this.password = password;
        this.mfaEnabled = mfaEnabled;
        this.accessToken = accessToken;
        this.deletion = deletion;
        this.disabled = disabled;
    }

    public void create() {
    }

    public void disable() {
    }

    public void delete() {
    }

    public void setAvatar() {

    }

    public String getId() {
        return id;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
