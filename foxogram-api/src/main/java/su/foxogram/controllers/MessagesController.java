package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.Message;
import su.foxogram.constructors.MessageRequest;
import su.foxogram.constructors.RequestMessage;
import su.foxogram.constructors.User;
import su.foxogram.enums.APIEnum;
import su.foxogram.repositories.MessageRepository;
import su.foxogram.services.AuthorizationService;
import su.foxogram.structures.Snowflake;

import java.util.List;

@RestController
@RequestMapping(value = APIEnum.MESSAGES, produces = "application/json")
public class MessagesController {

	private final MessageRepository messageRepository;
	private final AuthorizationService authorizationService;

	private final CassandraTemplate cassandraTemplate;

	@Autowired
	public MessagesController(MessageRepository messageRepository, AuthorizationService authorizationService, CassandraTemplate cassandraTemplate) {
		this.messageRepository = messageRepository;
		this.authorizationService = authorizationService;
		this.cassandraTemplate = cassandraTemplate;
	}

	@GetMapping("/channel/{channelId}")
	public ResponseEntity<?> getMessages(@PathVariable long channelId, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit, HttpServletRequest request) {
		User user = authorizationService.getUser(request);

		if (user == null || !user.isEmailVerified()) {
			return user == null ? authorizationService.handleNotAuthorized() : authorizationService.handleNotVerifiedEmail();
		}

		Query query = Query.query(Criteria.where("channelId").is(channelId)).withAllowFiltering();

		if (before > 0) {
			query = query.and(Criteria.where("timestamp").gt(before));
		}

		if (limit > 0) {
			query = query.limit(limit);
		}

		List<Message> messagesArray = cassandraTemplate.select(query, Message.class);

		if (messagesArray.isEmpty()) {
			return handleNotFound();
		}

		return ResponseEntity.ok(messagesArray);
	}

	@GetMapping("/channel/{channelId}/{id}")
	public ResponseEntity<?> getMessage(@PathVariable long channelId, @PathVariable long id, HttpServletRequest request) {
		User user = authorizationService.getUser(request);

		if (user == null || !user.isEmailVerified()) {
			return user == null ? authorizationService.handleNotAuthorized() : authorizationService.handleNotVerifiedEmail();
		}

		Message message = messageRepository.findByChannelIdAndId(channelId, id);

		if (message == null) {
			return handleNotFound();
		}

		return ResponseEntity.ok(message);
	}

	@PostMapping("/channel/{channelId}")
	public ResponseEntity<?> postMessage(@RequestBody MessageRequest body, @PathVariable long channelId, HttpServletRequest request) {
		User user = authorizationService.getUser(request);

		if (user == null || !user.isEmailVerified()) {
			return user == null ? authorizationService.handleNotAuthorized() : authorizationService.handleNotVerifiedEmail();
		}

		long id = new Snowflake(1).nextId();
		long authorId = user.getId();
		long timestamp = System.currentTimeMillis();
		List<String> attachments = body.getAttachments();
		String content = body.getContent();

		Message message = new Message(id, channelId, content, authorId, timestamp, attachments);
		messageRepository.save(message);

		return ResponseEntity.ok(message);
	}

	@DeleteMapping("/channel/{channelId}/{id}")
	public ResponseEntity<?> deleteMessage(@PathVariable long channelId, @PathVariable long id, HttpServletRequest request) {
		User user = authorizationService.getUser(request);

		if (user == null || !user.isEmailVerified()) {
			return user == null ? authorizationService.handleNotAuthorized() : authorizationService.handleNotVerifiedEmail();
		}

		Message message = messageRepository.findByChannelIdAndId(channelId, id);

		if (message == null) {
			return handleNotFound();
		}

		messageRepository.delete(message);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "Message has been deleted successfully").build());
	}

	@PatchMapping("/channel/{channelId}/{id}")
	public ResponseEntity<?> patchMessage(@RequestBody MessageRequest body, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) {
		User user = authorizationService.getUser(request);

		if (user == null || !user.isEmailVerified()) {
			return user == null ? authorizationService.handleNotAuthorized() : authorizationService.handleNotVerifiedEmail();
		}

		Message message = messageRepository.findByChannelIdAndId(channelId, id);

		if (message == null) {
			return handleNotFound();
		}

		String content = body.getContent();

		message.setContent(content);
		messageRepository.save(message);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "Message has been patched successfully").build());
	}

	private ResponseEntity<String> handleNotFound() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RequestMessage().setSuccess(false).addField("message", "Unable to find messages for this channel or matching these parameters").build());
	}
}
