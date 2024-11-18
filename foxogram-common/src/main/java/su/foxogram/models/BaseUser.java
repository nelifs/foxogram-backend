package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.UserConstants;

@Getter
@Setter
@MappedSuperclass
public class BaseUser {
    @Id
    public long id;

    @Column()
    public String username;

    @Column()
    public String accessToken;

    @Column()
    public String avatar;

    @Column()
    public long flags;

    @Column()
    public int type;

    @Column()
    public long createdAt;

    public BaseUser() {

    }

    public BaseUser(long id, String avatar, String username, String accessToken, long createdAt, long flags, int type) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.createdAt = createdAt;
        this.flags = flags;
        this.type = type;
        this.accessToken = accessToken;
    }

    public void addFlag(UserConstants.Flags flag) {
        this.flags |= flag.getBit();
    }

    public void removeFlag(UserConstants.Flags flag) {
        this.flags &= ~flag.getBit();
    }

    public boolean hasFlag(UserConstants.Flags flag) {
        return (this.flags & flag.getBit()) != 0;
    }
}
