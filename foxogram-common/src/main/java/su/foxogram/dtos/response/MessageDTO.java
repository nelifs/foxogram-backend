package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageDTO {
	private String id;

	private String content;

	private String authorId;

	private String channelId;

	private List<String> attachments;

	public MessageDTO(String id, String content, String authorId, String channelId, List<String> attachments) {
		this.id = id;
		this.content = content;
		this.authorId = authorId;
		this.channelId = channelId;
		this.attachments = attachments;
	}
}
