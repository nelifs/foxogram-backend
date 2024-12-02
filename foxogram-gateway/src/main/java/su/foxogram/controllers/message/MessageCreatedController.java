package su.foxogram.controllers.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import su.foxogram.dtos.MessagePayload;

@Controller
public class MessageCreatedController implements WebSocketHandler {

	final Logger logger = LoggerFactory.getLogger(MessageCreatedController.class);

	final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		logger.info("Session CREATED with ID {} and with URI {}", session.getId(), session.getUri());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

		MessagePayload payload = mapper.readValue((String) message.getPayload(), MessagePayload.class);
		logger.info("got MESSAGE CREATED event with CODE {} and NAME {}", payload.getCode(), payload.getName());
		session.sendMessage(new TextMessage("ok :)"));
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.error("There's an ERROR with transporting, SESSION ID {} and URI {}", session.getId(), session.getUri());
		throw new Exception(exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		logger.info("Session DESTROYED with STATUS {} ({}) and with ID {} and with URI {}", closeStatus.getReason(), closeStatus.getCode(), session.getId(), session.getUri());
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
