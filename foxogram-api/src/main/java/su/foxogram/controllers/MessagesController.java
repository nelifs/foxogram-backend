package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.*;
import su.foxogram.services.AuthenticationService;
import su.foxogram.services.ChannelsService;
import su.foxogram.services.MessagesService;

import java.util.List;

@RestController
@RequestMapping(value = APIEnum.MESSAGES, produces = "application/json")
public class MessagesController {
	private final AuthenticationService authenticationService;
	private final ChannelsService channelsService;
	private final MessagesService messagesService;
	Logger logger = LoggerFactory.getLogger(MessagesController.class);

	@Autowired
	public MessagesController(ChannelsService channelsService, AuthenticationService authenticationService, MessagesService messagesService) {
		this.channelsService = channelsService;
		this.authenticationService = authenticationService;
		this.messagesService = messagesService;
	}

	@GetMapping("/channel/{channelId}")
	public ResponseEntity<?> getMessages(@PathVariable long channelId, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit, HttpServletRequest request) throws UserEmailNotVerifiedException, MessageNotFoundException, UserNotFoundException, UserAuthenticationNeededException, MemberInChannelNotFoundException {
		User user = authenticationService.getUser(request, true, true);
		logger.info("MESSAGES ({}, {}) from CHANNEL ({}) by USER ({}, {}) requested", before, limit, channelId, user.getId(), user.getEmail());
		channelsService.getMemberInChannel(user.getId(), channelId);

		List<Message> messagesArray = messagesService.getMessages(before, limit, channelId);

		return ResponseEntity.ok(messagesArray);
	}

	@GetMapping("/channel/{channelId}/{id}")
	public ResponseEntity<?> getMessage(@PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws UserEmailNotVerifiedException, MessageNotFoundException, UserNotFoundException, UserAuthenticationNeededException, MemberInChannelNotFoundException, ChannelNotFoundException {
		User user = authenticationService.getUser(request, true, true);
		logger.info("MESSAGE ({}) from CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		Message message = messagesService.getMessage(id, channelId);

		return ResponseEntity.ok(message);
	}

	@PostMapping("/channel/{channelId}")
	public ResponseEntity<?> postMessage(@RequestBody MessageRequest body, @PathVariable long channelId, HttpServletRequest request) throws UserEmailNotVerifiedException, UserNotFoundException, UserAuthenticationNeededException, MemberInChannelNotFoundException, ChannelNotFoundException {
		User user = authenticationService.getUser(request, true, true);
		logger.info("MESSAGE post to CHANNEL ({}) by USER ({}, {}) requested", channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		Message message = messagesService.addMessage(channelId, user, body);

		return ResponseEntity.ok(message);
	}

	@DeleteMapping("/channel/{channelId}/{id}")
	public ResponseEntity<?> deleteMessage(@PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws UserEmailNotVerifiedException, MessageNotFoundException, UserNotFoundException, UserAuthenticationNeededException, MemberInChannelNotFoundException, ChannelNotFoundException {
		User user = authenticationService.getUser(request, true, true);
		logger.info("MESSAGE ({}) delete from CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		messagesService.deleteMessage(id, channelId);

		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "Message has been deleted successfully").build());
	}

	@PatchMapping("/channel/{channelId}/{id}")
	public ResponseEntity<?> patchMessage(@RequestBody MessageRequest body, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, UserEmailNotVerifiedException, UserNotFoundException, UserAuthenticationNeededException, MemberInChannelNotFoundException, ChannelNotFoundException {
		User user = authenticationService.getUser(request, true, true);
		logger.info("MESSAGE ({}) patch in CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		Message message = messagesService.editMessage(channelId, id, body);

		return ResponseEntity.ok(message);
	}
}
