package su.foxogram.payloads;

public class UserResumePayload {
	public String getResumeToken() {
		return resumeToken;
	}

	public void setResumeToken(String resumeToken) {
		this.resumeToken = resumeToken;
	}

	private String resumeToken;
}
