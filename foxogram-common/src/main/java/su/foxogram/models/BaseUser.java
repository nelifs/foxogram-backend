package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.UserConstants;
import su.foxogram.structures.Snowflake;

@Getter
@Setter
@MappedSuperclass
public class BaseUser {
    @Id
    public long id;

    @Column()
    public String displayName;

    @Column(unique = true)
    public String username;

    @Column()
    public String avatar;

    @Column()
    public long flags;

    @Column()
    public int type;

    public BaseUser() {

    }

    public BaseUser(long id, String avatar, String displayName, String username, long flags, int type) {
        this.id = id;
        this.avatar = avatar;
        this.displayName = displayName;
        this.username = username;
        this.flags = flags;
        this.type = type;
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
