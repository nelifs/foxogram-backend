package su.foxogram.constructors;

import java.util.List;

public class Channel {
    public String id, name;
    public List<User> users;
    public List<Message> messages;

    public Channel(String id, String name, List<User> users, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.messages = messages;
    }

    public void create() {

    }

    public void delete() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
