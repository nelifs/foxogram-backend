package su.foxogram.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import java.util.List;

public class BaseUser {

    @Id
    @PrimaryKey
    public long id;

    @Column("username")
    public String username;

    @Column("accesstoken")
    public String accessToken;

    @Column("avatar")
    public String avatar;

    @Column("flags")
    public List<String> flags;

    @Column("createdat")
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
