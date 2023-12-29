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

	@Column("digitcode")
	public String digitCode;

	@Column("lettercode")
	public String letterCode;

	@Column("verified")
	public boolean verified;

	public EmailVerification() {

	}

	public EmailVerification(long userId, String digitCode, String letterCode, boolean verified) {
		this.userId = userId;
		this.digitCode = digitCode;
		this.letterCode = letterCode;
		this.verified = verified;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}
}
