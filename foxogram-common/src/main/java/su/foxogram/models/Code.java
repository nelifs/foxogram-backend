package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("codes")
public class Code {

	@Column("userid")
	@PrimaryKey
	public long userId;

	@Column("type")
	public String type;

	@Column("value")
	public String value;

	@Column("expiresat")
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
