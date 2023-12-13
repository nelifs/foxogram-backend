package su.foxogram.constructors;

import java.util.List;

public class Channel {
    public String id;
    public List<User> users;
    public List<Message> messages;

    public Channel(String id, List<User> users, List<Message> messages) {
        this.id = id;
        this.users = users;
        this.messages = messages;
    }

    public Channel create() {
        return null;
    }

    public Channel modify() {
        return null;
    }

    public Channel delete() {
        return null;
    }

    public String getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
