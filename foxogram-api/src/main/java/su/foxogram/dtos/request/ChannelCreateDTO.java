package su.foxogram.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

@Setter
@Getter
public class ChannelCreateDTO {
    @Pattern(regexp = ValidationConstants.Regex.CHANNEL_NAME_REGEX, message = ValidationConstants.Messages.CHANNEL_NAME_INCORRECT)
    @Size(min = 1, max = ValidationConstants.Lengths.CHANNEL_NAME, message = ValidationConstants.Messages.CHANNEL_NAME_WRONG_LENGTH)
    private String name;

    @Min(value = 1, message = ValidationConstants.Messages.CHANNEL_TYPE_INCORRECT)
    @Max(value = 3, message = ValidationConstants.Messages.CHANNEL_TYPE_INCORRECT)
    @NotNull(message = ValidationConstants.Messages.CHANNEL_TYPE_NOT_NULL)
    private int type;
}
