package su.foxogram.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@Entity
@Table(name = "users")
public class User extends BaseUser {

    @Column()
    private String email;

    @Column()
    private boolean emailVerified;

    @Column()
    private String password;

    @Column()
    private String refreshToken;

    @Column()
    private long deletion;

    @Column()
    private boolean disabled;

    @Column()
    private boolean mfaEnabled;

    public User() {
    }

    public User(long id, String avatar, String username, String email, boolean emailVerified, String password, String accessToken, String refreshToken, long createdAt, List<String> flags, long deletion, boolean disabled, boolean mfaEnabled) {
        super(id, avatar, username, accessToken, createdAt, flags);
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.password = password;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.flags = flags;
        this.createdAt = createdAt;
        this.deletion = deletion;
        this.disabled = disabled;
        this.mfaEnabled = mfaEnabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
