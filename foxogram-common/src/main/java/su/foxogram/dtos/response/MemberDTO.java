package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Member;

@Getter
@Setter
public class MemberDTO {
    private long id;
    private String avatar;
    private String username;
    private long flags;
    private long type;
    private long permissions;

    public MemberDTO(Member member) {
        this.id = member.getId();
        this.avatar = member.getAvatar();
        this.username = member.getUsername();
        this.flags = member.getFlags();
        this.type = member.getType();
        this.permissions = member.getPermissions();
    }
}

