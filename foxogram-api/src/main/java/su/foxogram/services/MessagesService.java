package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constants.MemberConstants;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.models.*;
import su.foxogram.exceptions.MessageNotFoundException;
import su.foxogram.dtos.request.MessageDTO;
import su.foxogram.repositories.MessageRepository;
import su.foxogram.structures.Snowflake;

import java.util.List;

@Slf4j
@Service
public class MessagesService {

	private final MessageRepository messageRepository;

	@Autowired
	public MessagesService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
    }

	public List<Message> getMessages(long before, int limit, Channel channel) throws MessageNotFoundException {
		List<Message> messagesArray = messageRepository.findAllByChannel(channel);

		if (messagesArray.isEmpty()) throw new MessageNotFoundException();

		log.info("MESSAGES ({}, {}) in CHANNEL ({}) found successfully", limit, before, channel.getId());

		return messagesArray;
	}

	public Message getMessage(long id, Channel channel) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();

		log.info("MESSAGE ({}) in CHANNEL ({}) found successfully", id, channel.getId());

		return message;
	}

	public void addMessage(Channel channel, User user, MessageDTO body) {
		long id = new Snowflake(1).nextId();
		long authorId = user.getId();
		long timestamp = System.currentTimeMillis();
		List<String> attachments = body.getAttachments();
		String content = body.getContent();

		Message message = new Message(id, channel, content, authorId, timestamp, attachments);

		messageRepository.save(message);
		log.info("MESSAGE ({}) to CHANNEL ({}) saved to database successfully", id, channel.getId());
		log.info("MESSAGE ({}) in CHANNEL ({}) posted successfully", id, channel.getId());
    }

	public void deleteMessage(long id, Member member, Channel channel) throws MessageNotFoundException, MissingPermissionsException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();
		if (message.getAuthorId() != member.getId() || member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_MESSAGES)) throw new MissingPermissionsException();

		messageRepository.delete(message);
		log.info("MESSAGE ({}) in CHANNEL ({}) deleted successfully", id, channel.getId());
	}

	public Message editMessage(long id, Channel channel, Member member, MessageDTO body) throws MessageNotFoundException, MissingPermissionsException {
		Message message = messageRepository.findByChannelAndId(channel, id);
		String content = body.getContent();

		if (message == null) throw new MessageNotFoundException();
		if (message.getAuthorId() != member.getId()) throw new MissingPermissionsException();

		message.setContent(content);
		messageRepository.save(message);
		log.info("MESSAGE ({}) in CHANNEL ({}) patched successfully", id, channel.getId());

		return message;
	}
}
