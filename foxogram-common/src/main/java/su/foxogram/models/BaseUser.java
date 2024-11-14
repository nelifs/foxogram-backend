package su.foxogram.models;

import jakarta.persistence.*;

import java.util.List;

@MappedSuperclass
public class BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column()
    public String username;

    @Column()
    public String accessToken;

    @Column()
    public String avatar;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "flags", joinColumns = @JoinColumn(name = "user_id"))
    @Column()
    public List<String> flags;

    @Column()
    public long createdAt;

    public BaseUser() {

    }

    public BaseUser(long id, String avatar, String username, String accessToken, long createdAt, List<String> flags) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.createdAt = createdAt;
        this.flags = flags;
        this.accessToken = accessToken;
    }
}
