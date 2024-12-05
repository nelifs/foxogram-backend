package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MFAKeyDTO {
	private String key;

	public MFAKeyDTO(String key) {
		this.key = key;
	}
}
