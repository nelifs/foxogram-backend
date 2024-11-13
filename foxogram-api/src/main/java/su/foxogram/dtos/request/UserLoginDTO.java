package su.foxogram.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginDTO {
	@NotEmpty
	@Size(min = 2, max = 16)
	private String username;

	@Email
	@NotEmpty
	@Size(min = 4, max = 64)
	private String email;

	@NotEmpty
	@Size(min = 8, max = 128)
	private String password;
	private String resumeToken;
}
