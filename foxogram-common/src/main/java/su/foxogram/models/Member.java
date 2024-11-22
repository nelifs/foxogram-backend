package su.foxogram.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "members")
public class Member extends BaseUser {
    @Column()
    public long channelId;

    @Column()
    public boolean admin;

    public Member() {

    }

    public Member(long id, long channelId, String username, boolean admin, String avatar, long createdAt, long flags, int type) {
        super(id, avatar, username, createdAt, flags, type);
        this.channelId = channelId;
        this.admin = admin;
        this.type = type;
    }
}
