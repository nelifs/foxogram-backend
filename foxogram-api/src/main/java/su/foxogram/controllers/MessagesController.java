package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.response.MessagesDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.models.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.exceptions.*;
import su.foxogram.dtos.request.MessageDTO;
import su.foxogram.services.MessagesService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.MESSAGES, produces = "application/json")
public class MessagesController {

	private final MessagesService messagesService;

	@Autowired
	public MessagesController(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	@GetMapping("/channel/{channelId}")
	public MessagesDTO getMessages(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit, HttpServletRequest request) throws MessageNotFoundException {
		log.info("MESSAGES ({}, {}) from CHANNEL ({}) by USER ({}, {}) requested", before, limit, channel.getId(), user.getId(), user.getEmail());

		List<Message> messagesArray = messagesService.getMessages(before, limit, channel);

		return new MessagesDTO(messagesArray);
	}

	@GetMapping("/channel/{channelId}/{id}")
	public MessagesDTO getMessage(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel, @PathVariable long id) throws MessageNotFoundException {
		log.info("MESSAGE ({}) from CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.getMessage(id, channel));

		return new MessagesDTO(message);
	}

	@PostMapping("/channel/{channelId}")
	public OkDTO createMessage(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel, @Valid @RequestBody MessageDTO body) {
		log.info("MESSAGE post to CHANNEL ({}) by USER ({}, {}) requested", channel.getId(), user.getId(), user.getEmail());

		messagesService.addMessage(channel, user, body);

		return new OkDTO(true);
	}

	@DeleteMapping("/channel/{channelId}/{id}")
	public OkDTO deleteMessage(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "member") Member member, @RequestAttribute(value = "channel") Channel channel, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, MissingPermissionsException {
		log.info("MESSAGE ({}) delete from CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		messagesService.deleteMessage(id, member, channel);

		return new OkDTO(true);
	}

	@PatchMapping("/channel/{channelId}/{id}")
	public MessagesDTO editMessage(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "member") Member member, @RequestAttribute(value = "channel") Channel channel, @Valid @RequestBody MessageDTO body, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, MissingPermissionsException {
		log.info("MESSAGE ({}) patch in CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.editMessage(id, channel, member, body));

		return new MessagesDTO(message);
	}
}
