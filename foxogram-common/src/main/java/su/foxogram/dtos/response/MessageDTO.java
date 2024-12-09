package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageDTO {

	private String content;

	private String author;

	private String channel;

	private List<String> attachments;

	public MessageDTO(String content, String authorUsername, String channelName, List<String> attachments) {
		this.content = content;
		this.author = authorUsername;
		this.channel = channelName;
		this.attachments = attachments;
	}
}
