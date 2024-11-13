package su.foxogram.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginDTO {
	private String username;
	private String email;
    private String password;
	private String resumeToken;
}
