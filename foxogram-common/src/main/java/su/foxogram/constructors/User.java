package su.foxogram.constructors;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;


@Table("users")
public class User extends BaseUser {

    @Id
    @PrimaryKey
    private long id;

    @Column("avatar")
    private String avatar;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("emailverified")
    private boolean emailVerified;

    @Column("password")
    private String password;

    @Column("accesstoken")
    private String accessToken;

    @Column("flags")
    private List<String> flags;

    @Column("createdat")
    private long createdAt;

    @Column("deletion")
    private long deletion;

    @Column("disabled")
    private boolean disabled;

    @Column("mfaenabled")
    private boolean mfaEnabled;

    public User() {
    }

    public User(long id, String avatar, String username, String email, boolean emailVerified, String password, String accessToken, long createdAt, List<String> flags, long deletion, boolean disabled, boolean mfaEnabled) {
        super(id, avatar, username, accessToken, createdAt, flags);
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.password = password;
        this.accessToken = accessToken;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public long getDeletion() {
        return deletion;
    }

    public void setDeletion(long deletion) {
        this.deletion = deletion;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }
}
