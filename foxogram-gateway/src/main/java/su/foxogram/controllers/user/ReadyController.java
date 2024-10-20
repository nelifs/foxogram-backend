package su.foxogram.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import su.foxogram.constructors.User;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.payloads.ReadyPayload;
import su.foxogram.services.AuthenticationService;

import java.util.List;
import java.util.Map;

@Controller
public class ReadyController implements WebSocketHandler {

    AuthenticationService authenticationService;

    Logger logger = LoggerFactory.getLogger(ReadyController.class);
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ReadyController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Session CREATED with ID {} and with URI {}", session.getId(), session.getUri());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Map<String, List<String>> headers = session.getHandshakeHeaders();
        User user = authenticationService.getUser(headers.get("authorization").toString(), false, false);

        if (user == null) throw new UserNotFoundException();

        ReadyPayload payload = mapper.readValue((String) message.getPayload(), ReadyPayload.class);
        logger.info("got READY event with CODE {} and NAME {}", payload.getCode(), payload.getName());
        session.sendMessage(new TextMessage("ok :)"));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("There's an ERROR with transporting, SESSION ID {} and URI {}", session.getId(), session.getUri());
        throw new Exception(exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("Session DESTROYED with STATUS {} ({}) and with ID {} and with URI {}", closeStatus.getReason(), closeStatus.getCode(), session.getId(), session.getUri());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
