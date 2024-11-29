package su.foxogram.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.UserConstants;


@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    public long id;

    @Column()
    public String displayName;

    @Column(unique = true)
    public String username;

    @Column()
    public String avatar;

    @Column(unique = true)
    private String email;

    @Column()
    private String password;

    @Column()
    public long flags;

    @Column()
    public int type;

    @Column()
    private long deletion;

    public User() {
    }

    public User(long id, String avatar, String displayName, String username, String email, String password, long flags, int type, long deletion) {
        this.id = id;
        this.avatar = avatar;
        this.displayName = displayName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.flags = flags;
        this.type = type;
        this.deletion = deletion;
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
