package su.foxogram.payloads;

public class UserLoginPayload {
	private String username;
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResumeToken() {
		return resumeToken;
	}

	public void setResumeToken(String resumeToken) {
		this.resumeToken = resumeToken;
	}

	private String password;
	private String resumeToken;
}
