package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.UserConstants;

@Setter
@Getter
@Entity
@Table(name = "users", indexes = {
		@Index(name = "idx_user_id", columnList = "id", unique = true),
		@Index(name = "idx_user_username", columnList = "username", unique = true),
		@Index(name = "idx_user_email", columnList = "email", unique = true)
})
public class User {
	@Id
	public String id;

	@Column()
	public String displayName;

	@Column()
	public String username;

	@Column()
	public String avatar;

	@Column()
	public long flags;

	@Column()
	public int type;

	@Column()
	private String email;

	@Column()
	private String password;

	@Column()
	private long deletion;

	@Column()
	private String key;

	public User() {
	}

	public User(String id, String avatar, String displayName, String username, String email, String password, long flags, int type, long deletion, String key) {
		this.id = id;
		this.avatar = avatar;
		this.displayName = displayName;
		this.username = username.toLowerCase();
		this.email = email;
		this.password = password;
		this.flags = flags;
		this.type = type;
		this.deletion = deletion;
		this.key = key;
	}

	public void addFlag(UserConstants.Flags flag) {
		this.flags |= flag.getBit();
	}

	public void removeFlag(UserConstants.Flags flag) {
		this.flags &= ~flag.getBit();
	}

	public boolean hasFlag(UserConstants.Flags flag) {
		return (this.flags & flag.getBit()) != 0;
	}
}
