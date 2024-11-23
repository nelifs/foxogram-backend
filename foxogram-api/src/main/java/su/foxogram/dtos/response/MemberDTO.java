package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.Message;

import java.util.List;

@Getter
@Setter
public class MemberDTO {
    private long id;
    private String avatar;
    private String username;
    private long flags;
    private long type;
    private long createdAt;
    private boolean admin;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.avatar = member.getAvatar();
        this.username = member.getUsername();
        this.flags = member.getFlags();
        this.type = member.getType();
        this.createdAt = member.getCreatedAt();
        this.admin = member.isAdmin();
    }
}

