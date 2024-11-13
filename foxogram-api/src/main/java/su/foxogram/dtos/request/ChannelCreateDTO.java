package su.foxogram.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChannelCreateDTO {
    @NotEmpty
    @Size(min = 1, max = 16)
    private String name;
    private String type;
}
