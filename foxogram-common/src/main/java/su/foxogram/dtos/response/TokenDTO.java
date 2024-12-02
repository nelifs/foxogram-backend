package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {
	private String accessToken;

	public TokenDTO(String accessToken) {
		this.accessToken = accessToken;
	}
}
