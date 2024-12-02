package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Message;

import java.util.List;

@Getter
@Setter
public class MessagesDTO {
	private List<Message> messages;

	public MessagesDTO(List<Message> messages) {
		this.messages = messages;
	}
}
