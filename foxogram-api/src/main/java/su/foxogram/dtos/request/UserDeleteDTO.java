package su.foxogram.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.enums.ValidationEnum;

@Setter
@Getter
public class UserDeleteDTO {
	@Size(min = ValidationEnum.Lengths.MIN, max = ValidationEnum.Lengths.PASSWORD, message = ValidationEnum.Messages.PASSWORD_WRONG_LENGTH)
	private String password;
}
