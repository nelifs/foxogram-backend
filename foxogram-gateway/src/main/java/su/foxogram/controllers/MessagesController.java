package su.foxogram.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import su.foxogram.payloads.MessagePayload;

@Controller
public class MessagesController {

    @MessageMapping("/feed")
    @SendTo("/messages/feed")
    public void newMessage(MessagePayload payload) {

    }

    @MessageMapping("/edit")
    @SendTo("/messages/edit")
    public void editMessage(MessagePayload payload) {

    }
}
