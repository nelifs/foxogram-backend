package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "messages", indexes = {
		@Index(name = "idx_message_id", columnList = "id", unique = true),
		@Index(name = "idx_message_channel", columnList = "channel", unique = true),
		@Index(name = "idx_message__id_channel_id", columnList = "id, channel", unique = true)
})
public class Message {
	@Id
	public String id;

	@Column()
	public String content;

	@Column()
	public String authorId;

	@Column()
	public long timestamp;

	@ElementCollection(targetClass = String.class)
	@CollectionTable(name = "attachments", joinColumns = @JoinColumn(name = "message_id"))
	@Column()
	public List<String> attachments;

	@ManyToOne
	@JoinColumn(name = "channel", nullable = false)
	private Channel channel;

	public Message() {

	}

	public Message(String id, Channel channel, String content, String authorId, long timestamp, List<String> attachments) {
		this.id = id;
		this.channel = channel;
		this.authorId = authorId;
		this.content = content;
		this.timestamp = timestamp;
		this.attachments = attachments;
	}

}
