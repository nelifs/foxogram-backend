package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Attachment {
	public String etag, hash;

	public Attachment(String etag, String hash) {
		this.etag = etag;
		this.hash = hash;
	}
}
