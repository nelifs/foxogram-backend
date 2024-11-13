package su.foxogram.dtos.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MessageDTO {
	private String content;
	private List<String> attachments;
}
