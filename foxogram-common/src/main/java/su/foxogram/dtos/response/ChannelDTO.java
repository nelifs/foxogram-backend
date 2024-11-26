package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;
import su.foxogram.models.Channel;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ChannelDTO {
    private long id;
    private String name;
    private int type;
    private long ownerId;

    public ChannelDTO(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.type = channel.getType();
        this.ownerId = channel.getOwnerId();
    }
}

