package su.foxogram.constructors;

public class Session {
    public String accessToken, resumeToken;
    public long createdAt, expiresIn;

    public Session(String accessToken, String resumeToken, long createdAt, long expiresIn) {
        this.accessToken = accessToken;
        this.resumeToken = resumeToken;
        this.createdAt = createdAt;
        this.expiresIn = expiresIn;
    }

    public void create() {
    }

    public void delete() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getResumeToken() {
        return resumeToken;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
