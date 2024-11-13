package su.foxogram.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSignUpDTO {
	private String username;
	private String email;
    private String password;
}
