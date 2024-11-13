package su.foxogram.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChannelCreateDTO {
    private String name;
    private String type;
}
