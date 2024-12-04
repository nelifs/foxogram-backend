package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.User;

@Getter
@Setter
public class UserDTO {
	private String id;

	private String avatar;

	private String displayName;

	private String username;

	private long flags;

	private long type;

	public UserDTO(User user) {
		this.id = user.getId();
		this.avatar = user.getAvatar();
		this.displayName = user.getDisplayName();
		this.username = user.getUsername();
		this.flags = user.getFlags();
		this.type = user.getType();
	}
}
