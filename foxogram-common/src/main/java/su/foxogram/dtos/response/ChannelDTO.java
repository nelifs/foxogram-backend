package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Channel;

@Getter
@Setter
public class ChannelDTO {
	private String id;

	private String name;

	private int type;

	private String ownerId;

	public ChannelDTO(Channel channel) {
		this.id = channel.getId();
		this.name = channel.getName();
		this.type = channel.getType();
		this.ownerId = channel.getOwnerId();
	}
}
