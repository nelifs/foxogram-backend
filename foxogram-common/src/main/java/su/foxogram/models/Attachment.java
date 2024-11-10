package su.foxogram.models;

public class Attachment {
	public String etag, hash;

	public Attachment(String etag, String hash) {
		this.etag = etag;
		this.hash = hash;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
