package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Setter
@Getter
@Table("sessions")
public class Session {

	@Id
	@PrimaryKey
	public long id;

	@Column("accesstoken")
	public String accessToken;

	@Column("createdat")
	public long createdAt;

	@Column("expiresat")
	public long expiresAt;

	public Session() {

	}

	public Session(long id, String accessToken, long createdAt, long expiresAt) {
		this.id = id;
		this.accessToken = accessToken;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

    public boolean isExpired() {
		return System.currentTimeMillis() >= this.getExpiresAt();
	}
}
