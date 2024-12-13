package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.request.MessageCreateDTO;
import su.foxogram.dtos.response.MessageDTO;
import su.foxogram.dtos.response.MessagesDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.exceptions.MessageNotFoundException;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.exceptions.UploadFailedException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.services.MessagesService;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Messages")
@RequestMapping(value = APIConstants.MESSAGES, produces = "application/json")
public class MessagesController {

	private final MessagesService messagesService;

	@Autowired
	public MessagesController(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	@Operation(summary = "Get messages")
	@GetMapping("/channel/{name}")
	public List<MessageDTO> getMessages(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit) {
		log.info("MESSAGES ({}, {}) from CHANNEL ({}) by USER ({}, {}) requested", before, limit, channel.getId(), user.getId(), user.getEmail());

		return messagesService.getMessages(before, limit, channel);
	}

	@Operation(summary = "Get message")
	@GetMapping("/channel/{name}/{id}")
	public MessagesDTO getMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id) throws MessageNotFoundException {
		log.info("MESSAGE ({}) from CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.getMessage(id, channel));

		return new MessagesDTO(message);
	}

	@Operation(summary = "Create message")
	@PostMapping("/channel/{name}")
	public OkDTO createMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @RequestBody MessageCreateDTO body) throws UploadFailedException {
		log.info("MESSAGE post to CHANNEL ({}) by USER ({}, {}) requested", channel.getId(), user.getId(), user.getEmail());

		messagesService.addMessage(channel, user, body);

		return new OkDTO(true);
	}

	@Operation(summary = "Delete message")
	@DeleteMapping("/channel/{name}/{id}")
	public OkDTO deleteMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id) throws MessageNotFoundException, MissingPermissionsException {
		log.info("MESSAGE ({}) delete from CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		messagesService.deleteMessage(id, member, channel);

		return new OkDTO(true);
	}

	@Operation(summary = "Edit message")
	@PatchMapping("/channel/{name}/{id}")
	public MessagesDTO editMessage(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @RequestBody MessageCreateDTO body, @PathVariable long id) throws MessageNotFoundException, MissingPermissionsException {
		log.info("MESSAGE ({}) patch in CHANNEL ({}) by USER ({}, {}) requested", id, channel.getId(), user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.editMessage(id, channel, member, body));

		return new MessagesDTO(message);
	}
}
