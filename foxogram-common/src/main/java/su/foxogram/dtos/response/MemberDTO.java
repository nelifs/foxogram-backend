package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Member;

@Getter
@Setter
public class MemberDTO {
	private String id;

	private String channelId;

	private long permissions;

	public MemberDTO(Member member) {
		this.id = member.getId();
		this.channelId = member.getChannel().getId();
		this.permissions = member.getPermissions();
	}
}

