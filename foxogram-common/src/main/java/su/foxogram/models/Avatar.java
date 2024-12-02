package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Avatar {
	public String id;

	public Avatar(String id) {
		this.id = id;
	}
}
