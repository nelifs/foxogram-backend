package su.foxogram.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.User;

@Getter
@Setter
public class UserDTO {

	private String avatar;

	private String displayName;

	private String username;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;

	private long flags;

	private long type;

	private long createdAt;

	public UserDTO(User user, boolean includeEmail) {
		this.avatar = user.getAvatar();
		this.displayName = user.getDisplayName();
		this.username = user.getUsername();
		if (includeEmail) {
			this.email = user.getEmail();
		}
		this.flags = user.getFlags();
		this.type = user.getType();
		this.createdAt = user.getCreatedAt();
	}
}
