package su.foxogram.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.enums.ValidationEnum;

@Setter
@Getter
public class ChannelCreateDTO {
    @Pattern(regexp = ValidationEnum.Regex.CHANNEL_NAME_REGEX, message = ValidationEnum.Messages.CHANNEL_NAME_INCORRECT)
    @Size(min = 1, max = ValidationEnum.Lengths.CHANNEL_NAME, message = ValidationEnum.Messages.CHANNEL_NAME_WRONG_LENGTH)
    private String name;
    private String type;
}
