package su.foxogram.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
