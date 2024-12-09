package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import su.foxogram.constants.MemberConstants;

@Setter
@Getter
@Entity
@Table(name = "members", indexes = {
		@Index(name = "idx_member_user_channel_id", columnList = "user_id, channel")
})
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	public String username;

	@Column()
	public long permissions;

	@MapsId
	@ManyToOne()
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne()
	@JoinColumn(name = "channel", nullable = false)
	private Channel channel;

	public Member() {

	}

	public Member(long id) {
		this.id = id;
	}

	public Member(User user, Channel channel, long permissions) {
		this.username = user.getUsername();
		this.user = user;
		this.channel = channel;
		this.permissions = permissions;
	}

	public void addPermission(MemberConstants.Permissions permission) {
		this.permissions |= permission.getBit();
	}

	public void removePermission(MemberConstants.Permissions permission) {
		this.permissions &= ~permission.getBit();
	}

	public boolean hasPermission(MemberConstants.Permissions permission) {
		return (this.permissions & permission.getBit()) != 0;
	}

	public boolean hasPermissions(MemberConstants.Permissions... permissions) {
		for (MemberConstants.Permissions permission : permissions) {
			if ((this.permissions & permission.getBit()) == 0) {
				return false;
			}
		}
		return true;
	}

	public boolean hasAnyPermission(MemberConstants.Permissions... permissions) {
		for (MemberConstants.Permissions permission : permissions) {
			if ((this.permissions & permission.getBit()) != 0) {
				return false;
			}
		}
		return true;
	}
}
