package su.foxogram.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.enums.ValidationEnum;

@Setter
@Getter
public class UserLoginDTO {
	@Size(min = ValidationEnum.Lengths.MIN, max = ValidationEnum.Lengths.USERNAME, message = ValidationEnum.Messages.USERNAME_WRONG_LENGTH)
	@Pattern(regexp = ValidationEnum.Regex.USERNAME_REGEX, message = ValidationEnum.Messages.USERNAME_INCORRECT)
	private String username;

	@Size(min = ValidationEnum.Lengths.MIN, max = ValidationEnum.Lengths.EMAIL, message = ValidationEnum.Messages.EMAIL_WRONG_LENGTH)
	@Pattern(regexp = ValidationEnum.Regex.EMAIL_REGEX, message = ValidationEnum.Messages.EMAIL_INCORRECT)
	private String email;

	@Size(min = ValidationEnum.Lengths.MIN, max = ValidationEnum.Lengths.PASSWORD, message = ValidationEnum.Messages.PASSWORD_WRONG_LENGTH)
	private String password;
	private String resumeToken;
}
