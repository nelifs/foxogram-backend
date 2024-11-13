package su.foxogram.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Setter
@Getter
@Table("messages")
public class Message {
    @Id
    @PrimaryKey
    public long id;

    @Column("channelid")
    public long channelId;

    @Column("content")
    public String content;

    @Column("authorid")
    public long authorId;

    @Column("timestamp")
    public long timestamp;

    @Column("attachments")
    public List<String> attachments;

    public Message() {

    }

    public Message(long id, long channelId, String content, long authorId, long timestamp, List<String> attachments) {
        this.id = id;
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
        this.attachments = attachments;
    }

}
