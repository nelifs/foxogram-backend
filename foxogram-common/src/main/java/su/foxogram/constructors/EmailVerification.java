package su.foxogram.constructors;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("emailverify")
public class EmailVerification {

	@Column("userid")
	@PrimaryKey
	public long userId;

	@Column("type")
	public String type;

	@Column("digitcode")
	public String digitCode;

	@Column("lettercode")
	public String letterCode;

	public EmailVerification() {

	}

	public EmailVerification(long userId, String type, String digitCode, String letterCode) {
		this.userId = userId;
		this.type = type;
		this.digitCode = digitCode;
		this.letterCode = letterCode;
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

	public String getDigitCode() {
		return digitCode;
	}

	public void setDigitCode(String digitCode) {
		this.digitCode = digitCode;
	}

	public String getLetterCode() {
		return letterCode;
	}

	public void setLetterCode(String letterCode) {
		this.letterCode = letterCode;
	}
}
