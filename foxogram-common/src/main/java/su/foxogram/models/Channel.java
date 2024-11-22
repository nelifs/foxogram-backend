package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    public String type;

    @Column()
    public long ownerId;

    public Channel() {

    }

    public Channel(long id, String name, String type, long ownerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.ownerId = ownerId;
    }
}
