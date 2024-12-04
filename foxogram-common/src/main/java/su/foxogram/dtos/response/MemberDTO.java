package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Member;
import su.foxogram.models.User;

@Getter
@Setter
public class MemberDTO {
	private String id;

	private String avatar;

	private String username;

	private long flags;

	private long type;

	private long permissions;

	public MemberDTO(Member member) {
		User user = member.getUser();

		this.id = member.getId();
		this.avatar = user.getAvatar();
		this.username = user.getUsername();
		this.flags = user.getFlags();
		this.type = user.getType();
		this.permissions = member.getPermissions();
	}
}

