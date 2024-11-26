package su.foxogram.dtos.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OkDTO {
    private boolean ok;

    public OkDTO(boolean ok) {
        this.ok = ok;
    }
}
