package su.foxogram.constructors;

import java.util.List;

public class Message {
    public String id, content, authorId;
    public long timestamp;
    public List<Attachment> attachments;

    public Message(String id, String content, String authorId, long timestamp, List<Attachment> attachments) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
        this.attachments = attachments;
    }

    public Message create() {
        return null;
    }

    public Message modify() {
        return null;
    }

    public Message delete() {
        return null;
    }

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}
