package su.foxogram.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "messages")
public class Message {
    @Id
    public long id;

    @ManyToOne
    @JoinColumn(name = "channel", nullable = false)
    private Channel channel;

    @Column()
    public String content;

    @Column()
    public long authorId;

    @Column()
    public long timestamp;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "attachments", joinColumns = @JoinColumn(name = "message_id"))
    @Column()
    public List<String> attachments;

    public Message() {

    }

    public Message(long id, Channel channel, String content, long authorId, long timestamp, List<String> attachments) {
        this.id = id;
        this.channel = channel;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
        this.attachments = attachments;
    }

}
