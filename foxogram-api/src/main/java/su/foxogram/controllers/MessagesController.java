package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.request.MessageCreateDTO;
import su.foxogram.dtos.response.MessagesDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.exceptions.MessageNotFoundException;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.Message;
import su.foxogram.models.User;
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
	public MessagesDTO getMessages(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit, HttpServletRequest request) throws MessageNotFoundException {
		log.info("MESSAGES ({}, {}) from CHANNEL ({}) by USER ({}, {}) requested", before, limit, channel.getId(), user.getId(), user.getEmail());

		List<Message> messagesArray = messagesService.getMessages(before, limit, channel);

		return new MessagesDTO(messagesArray);
	}

	@GetMapping("/channel/{channelId}/{id}")
	public MessagesDTO getMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable String id) throws MessageNotFoundException {
		log.info("MESSAGE ({}) from CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.getMessage(id, channel));

		return new MessagesDTO(message);
	}

	@PostMapping("/channel/{channelId}")
	public OkDTO createMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @RequestBody MessageCreateDTO body) {
		log.info("MESSAGE post to CHANNEL ({}) by USER ({}, {}) requested", channel.getId(), user.getId(), user.getEmail());

		messagesService.addMessage(channel, user, body);

		return new OkDTO(true);
	}

	@DeleteMapping("/channel/{channelId}/{id}")
	public OkDTO deleteMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable String id, HttpServletRequest request) throws MessageNotFoundException, MissingPermissionsException {
		log.info("MESSAGE ({}) delete from CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		messagesService.deleteMessage(id, member, channel);

		return new OkDTO(true);
	}

	@PatchMapping("/channel/{channelId}/{id}")
	public MessagesDTO editMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @RequestBody MessageCreateDTO body, @PathVariable String id, HttpServletRequest request) throws MessageNotFoundException, MissingPermissionsException {
		log.info("MESSAGE ({}) patch in CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.editMessage(id, channel, member, body));

		return new MessagesDTO(message);
	}
}
