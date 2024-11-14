package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "codes")
public class Code {
	@Id()
	public long userId;

	@Column()
	public String type;

	@Column()
	public String value;

	@Column()
	public long expiresAt;

	public Code() {

	}

	public Code(long userId, String type, String value, long expiresAt) {
		this.userId = userId;
		this.type = type;
		this.value = value;
		this.expiresAt = expiresAt;
	}
}
