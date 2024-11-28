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
	public MessagesDTO getMessages(@RequestAttribute(value = "user") User user, @PathVariable long channelId, @RequestParam(required = false, defaultValue = "0") long before, @RequestParam(required = false, defaultValue = "0") int limit, HttpServletRequest request) throws MessageNotFoundException {
		log.info("MESSAGES ({}, {}) from CHANNEL ({}) by USER ({}, {}) requested", before, limit, channelId, user.getId(), user.getEmail());

		List<Message> messagesArray = messagesService.getMessages(before, limit, channelId);

		return new MessagesDTO(messagesArray);
	}

	@GetMapping("/channel/{channelId}/{id}")
	public MessagesDTO getMessage(@RequestAttribute(value = "user") User user, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, ChannelNotFoundException {
		log.info("MESSAGE ({}) from CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.getMessage(id, channelId));

		return new MessagesDTO(message);
	}

	@PostMapping("/channel/{channelId}")
	public OkDTO postMessage(@RequestAttribute(value = "user") User user, @Valid @RequestBody MessageDTO body, @PathVariable long channelId, HttpServletRequest request) throws ChannelNotFoundException {
		log.info("MESSAGE post to CHANNEL ({}) by USER ({}, {}) requested", channelId, user.getId(), user.getEmail());

		messagesService.addMessage(channelId, user, body);

		return new OkDTO(true);
	}

	@DeleteMapping("/channel/{channelId}/{id}")
	public OkDTO deleteMessage(@RequestAttribute(value = "user") User user, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, ChannelNotFoundException {
		log.info("MESSAGE ({}) delete from CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		messagesService.deleteMessage(id, channelId);

		return new OkDTO(true);
	}

	@PatchMapping("/channel/{channelId}/{id}")
	public MessagesDTO patchMessage(@RequestAttribute(value = "user") User user, @Valid @RequestBody MessageDTO body, @PathVariable long channelId, @PathVariable long id, HttpServletRequest request) throws MessageNotFoundException, ChannelNotFoundException {
		log.info("MESSAGE ({}) patch in CHANNEL ({}) by USER ({}, {}) requested", id, channelId, user.getId(), user.getEmail());

		List<Message> message = List.of(messagesService.editMessage(channelId, id, body));

		return new MessagesDTO(message);
	}
}
