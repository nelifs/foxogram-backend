package su.foxogram.constructors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("sessions")
public class Session {

	@Id
	@PrimaryKey
	public long id;

	@Column("accesstoken")
	public String accessToken;

	@Column("resumetoken")
	public String resumeToken;

	@Column("createdat")
	public long createdAt;

	@Column("expiresat")
	public long expiresAt;

	public Session() {

	}

	public Session(long id, String accessToken, String resumeToken, long createdAt, long expiresAt) {
		this.id = id;
		this.accessToken = accessToken;
		this.resumeToken = resumeToken;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getResumeToken() {
		return resumeToken;
	}

	public void setResumeToken(String resumeToken) {
		this.resumeToken = resumeToken;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() >= this.getExpiresAt();
	}
}
