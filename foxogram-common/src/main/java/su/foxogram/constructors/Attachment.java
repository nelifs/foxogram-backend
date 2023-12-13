package su.foxogram.constructors;

public class Attachment {
    public String etag, hash;

    public Attachment(String etag, String hash) {
        this.etag = etag;
        this.hash = hash;
    }

    public Attachment create() {
        return null;
    }

    public Attachment delete() {
        return null;
    }

    public String getEtag() {
        return etag;
    }

    public String getHash() {
        return hash;
    }
}
