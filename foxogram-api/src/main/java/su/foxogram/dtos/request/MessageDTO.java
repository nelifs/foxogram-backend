package su.foxogram.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.ValidationConstants;

import java.util.List;

@Setter
@Getter
public class MessageDTO {
	@NotNull
	@Size(min = 1, max = ValidationConstants.Lengths.MESSAGE_CONTENT, message = ValidationConstants.Messages.MESSAGE_WRONG_LENGTH)
	private String content;
	private List<String> attachments;
}
