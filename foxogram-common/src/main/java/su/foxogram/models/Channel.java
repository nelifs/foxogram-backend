package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "channels")
public class Channel {
    @Id
    public long id;

    @Column()
    public String name;

    @Column()
    public int type;

    @Column()
    public long ownerId;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    public Channel() {

    }

    public Channel(long id, String name, int type, long ownerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.ownerId = ownerId;
    }
}
