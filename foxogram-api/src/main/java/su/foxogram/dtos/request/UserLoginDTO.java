package su.foxogram.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Setter
@Getter
public class UserLoginDTO {
	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.EMAIL, message = ValidationConstants.Messages.EMAIL_WRONG_LENGTH)
	@Pattern(regexp = ValidationConstants.Regex.EMAIL_REGEX, message = ValidationConstants.Messages.EMAIL_INCORRECT)
	private String email;

	@Size(min = ValidationConstants.Lengths.MIN, max = ValidationConstants.Lengths.PASSWORD, message = ValidationConstants.Messages.PASSWORD_WRONG_LENGTH)
	private String password;
	private String resumeToken;
}
