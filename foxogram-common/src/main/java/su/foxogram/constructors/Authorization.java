package su.foxogram.constructors;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("authorizations")
public class Authorization {

	@Id
	@PrimaryKey
	public long id;

	@Column("accesstoken")
	public String accessToken;

	@Column("createdat")
	public long createdAt;

	@Column("expiresat")
	public long expiresAt;

	public Authorization() {

	}

	public Authorization(long id, String accessToken, long createdAt, long expiresAt) {
		this.id = id;
		this.accessToken = accessToken;
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
