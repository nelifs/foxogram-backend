package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.response.MessagesDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.models.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.*;
import su.foxogram.dtos.request.MessageDTO;
import su.foxogram.services.AuthenticationService;
import su.foxogram.services.ChannelsService;
import su.foxogram.services.MessagesService;

import java.util.List;

@RestController
@RequestMapping(value = APIEnum.MESSAGES, produces = "application/json")
public class MessagesController {

    private final ChannelsService channelsService;
	private final MessagesService messagesService;
	final Logger logger = LoggerFactory.getLogger(MessagesController.class);

	@Autowired
	public MessagesController(ChannelsService channelsService, AuthenticationService authenticationService, MessagesService messagesService) {
		this.channelsService = channelsService;
        this.messagesService = messagesService;
	}

	@GetMapping("/channel/{channelId}")
	public MessagesDTO getMessages(@RequestAttribute(value = "user") User user, @PathVariable long channelId, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit, HttpServletRequest request) throws MessageNotFoundException, MemberInChannelNotFoundException {
		logger.info("MESSAGES ({}, {}) from CHANNEL ({}) by USER ({}, {}) requested", before, limit, channelId, user.getId(), user.getEmail());
		channelsService.getMemberInChannel(user.getId(), channelId);

		List<Message> messagesArray = messagesService.getMessages(before, limit, channelId);

		return new MessagesDTO(messagesArray);
	}

	@GetMapping("/channel/{channelId}/{id}")
	public MessagesDTO getMessage(@RequestAttribute(value = "user") User user, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, MemberInChannelNotFoundException, ChannelNotFoundException {
		logger.info("MESSAGE ({}) from CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		List<Message> message = List.of(messagesService.getMessage(id, channelId));

		return new MessagesDTO(message);
	}

	@PostMapping("/channel/{channelId}")
	public MessagesDTO postMessage(@RequestAttribute(value = "user") User user, @Valid @RequestBody MessageDTO body, @PathVariable long channelId, HttpServletRequest request) throws MemberInChannelNotFoundException, ChannelNotFoundException {
		logger.info("MESSAGE post to CHANNEL ({}) by USER ({}, {}) requested", channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		List<Message> message = List.of(messagesService.addMessage(channelId, user, body));

		return new MessagesDTO(message);
	}

	@DeleteMapping("/channel/{channelId}/{id}")
	public OkDTO deleteMessage(@RequestAttribute(value = "user") User user, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, MemberInChannelNotFoundException, ChannelNotFoundException {
		logger.info("MESSAGE ({}) delete from CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		messagesService.deleteMessage(id, channelId);

		return new OkDTO(true);
	}

	@PatchMapping("/channel/{channelId}/{id}")
	public MessagesDTO patchMessage(@RequestAttribute(value = "user") User user, @Valid @RequestBody MessageDTO body, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, MemberInChannelNotFoundException, ChannelNotFoundException {
		logger.info("MESSAGE ({}) patch in CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		channelsService.getMemberInChannel(user.getId(), channelId);

		List<Message> message = List.of(messagesService.editMessage(channelId, id, body));

		return new MessagesDTO(message);
	}
}
