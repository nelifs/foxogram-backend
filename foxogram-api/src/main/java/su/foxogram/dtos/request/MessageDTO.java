package su.foxogram.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.enums.ValidationEnum;

import java.util.List;

@Setter
@Getter
public class MessageDTO {
	@Size(min = 1, max = ValidationEnum.Lengths.MESSAGE_CONTENT, message = ValidationEnum.Messages.MESSAGE_WRONG_LENGTH)
	private String content;
	private List<String> attachments;
}
