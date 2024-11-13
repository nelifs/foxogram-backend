package su.foxogram.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MessageDTO {
	@Size(min = 1, max = 5000)
	private String content;
	private List<String> attachments;
}
