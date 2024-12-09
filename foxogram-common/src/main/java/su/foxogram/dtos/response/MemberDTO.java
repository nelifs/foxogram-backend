package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Member;

@Getter
@Setter
public class MemberDTO {
	private String channel;

	private long permissions;

	public MemberDTO(Member member) {
		this.channel = member.getChannel().getName();
		this.permissions = member.getPermissions();
	}
}

