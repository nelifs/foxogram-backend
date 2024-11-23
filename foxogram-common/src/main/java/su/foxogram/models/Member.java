package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "members")
public class Member extends BaseUser {
    @ManyToOne
    @JoinColumn(name = "channel", nullable = false)
    private Channel channel;

    @Column()
    public boolean admin;

    public Member() {

    }

    public Member(long id, Channel channel, String username, boolean admin, String avatar, long createdAt, long flags, int type) {
        super(id, avatar, username, createdAt, flags, type);
        this.channel = channel;
        this.admin = admin;
        this.type = type;
    }
}
