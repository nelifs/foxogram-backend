package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("channels")
public class Channel {
    @Id
    @PrimaryKey
    public long id;

    @Column("name")
    public String name;

    @Column("type")
    public String type;

    @Column("ownerid")
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
