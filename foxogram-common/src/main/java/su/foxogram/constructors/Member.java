package su.foxogram.constructors;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table("members")
public class Member extends BaseUser {

    @Id
    @PrimaryKey
    public long id;

    @Column("channelid")
    public long channelId;

    @Column("username")
    public String username;

    @Column("accesstoken")
    public String accessToken;

    @Column("admin")
    public boolean admin;

    @Column("avatar")
    public String avatar;

    @Column("createdat")
    public long createdAt;

    @Column("flags")
    public List<String> flags;

    public Member() {

    }

    public Member(long id, long channelId, String username, String accessToken, boolean admin, String avatar, long createdAt, List<String> flags) {
        super(id, avatar, username, accessToken, createdAt, flags);
        this.channelId = channelId;
        this.admin = admin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }
}
