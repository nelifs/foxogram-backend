package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sessions")
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long id;

	@Column()
	public String accessToken;

	@Column()
	public long createdAt;

	@Column()
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
