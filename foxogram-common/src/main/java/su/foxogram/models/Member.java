package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Setter
@Getter
@Table("members")
public class Member extends BaseUser {

    @Column("channelid")
    public long channelId;

    @Column("admin")
    public boolean admin;

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
