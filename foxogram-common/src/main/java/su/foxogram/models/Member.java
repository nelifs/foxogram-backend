package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.MemberConstants;

@Setter
@Getter
@Entity
@Table(name = "members")
public class Member extends BaseUser {
    @ManyToOne
    @JoinColumn(name = "channel", nullable = false)
    private Channel channel;

    @Column()
    public long permissions;

    public Member() {

    }

    public Member(long id, Channel channel, String username, long permissions, String avatar, long createdAt, long flags, int type) {
        super(id, avatar, username, createdAt, flags, type);
        this.channel = channel;
        this.permissions = permissions;
        this.type = type;
    }

    public void addPermission(MemberConstants.Permissions permission) {
        this.permissions |= permission.getBit();
    }

    public void removePermission(MemberConstants.Permissions permission) {
        this.permissions &= ~permission.getBit();
    }

    public boolean hasPermission(MemberConstants.Permissions permission) {
        return (this.permissions & permission.getBit()) != 0;
    }
}
