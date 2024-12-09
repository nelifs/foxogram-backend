package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Channel;

@Getter
@Setter
public class ChannelDTO {

	private String name;

	private int type;

	private String owner;

	public ChannelDTO(Channel channel) {
		this.name = channel.getName();
		this.type = channel.getType();
		this.owner = channel.getOwner();
	}
}
