package su.foxogram.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import su.foxogram.payloads.ReadyPayload;

@Controller
public class ReadyController {

    @MessageMapping("/ready")
    @SendTo("/")
    public void ready(ReadyPayload payload) {

    }
}
