package su.foxogram.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Getter
@Setter
public class ChannelEditDTO {
	@NotNull(message = "Name" + ValidationConstants.Messages.MUST_NOT_BE_NULL)
	@Pattern(regexp = ValidationConstants.Regex.CHANNEL_NAME_REGEX, message = ValidationConstants.Messages.CHANNEL_NAME_INCORRECT)
	@Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
	private String name;
}
