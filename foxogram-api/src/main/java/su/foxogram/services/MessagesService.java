package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constants.MemberConstants;
import su.foxogram.dtos.request.MessageCreateDTO;
import su.foxogram.dtos.response.MessageDTO;
import su.foxogram.exceptions.MessageNotFoundException;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.Message;
import su.foxogram.models.User;
import su.foxogram.repositories.MessageRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessagesService {

	private final MessageRepository messageRepository;

	@Autowired
	public MessagesService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public List<MessageDTO> getMessages(long before, int limit, Channel channel) {
		List<Message> messagesArray = messageRepository.findAllByChannel(channel, limit);

		log.info("MESSAGES ({}, {}) in CHANNEL ({}) found successfully", limit, before, channel.getId());

		return messagesArray.stream()
				.limit(limit)
				.map(message -> new MessageDTO(
						message.getId(),
						message.getContent(),
						message.getAuthor().getUser().getUsername(),
						message.getChannel().getName(),
						message.getAttachments()
				))
				.collect(Collectors.toList());
	}

	public Message getMessage(long id, Channel channel) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();

		log.info("MESSAGE ({}) in CHANNEL ({}) found successfully", id, channel.getId());

		return message;
	}

	public void addMessage(Channel channel, User user, MessageCreateDTO body) {
		long authorId = user.getId();
		long timestamp = System.currentTimeMillis();
		List<String> attachments = body.getAttachments();
		String content = body.getContent();

		Message message = new Message(0, channel, content, authorId, timestamp, attachments);

		messageRepository.save(message);
		log.info("MESSAGE ({}) to CHANNEL ({}) saved to database successfully", message.getId(), channel.getId());
	}

	public void deleteMessage(long id, Member member, Channel channel) throws MessageNotFoundException, MissingPermissionsException {
		Message message = messageRepository.findByChannelAndId(channel, id);

		if (message == null) throw new MessageNotFoundException();
		if (!Objects.equals(message.getAuthor().getUser().getUsername(), member.getUser().getUsername()) || member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_MESSAGES))
			throw new MissingPermissionsException();

		messageRepository.delete(message);
		log.info("MESSAGE ({}) in CHANNEL ({}) deleted successfully", id, channel.getId());
	}

	public Message editMessage(long id, Channel channel, Member member, MessageCreateDTO body) throws MessageNotFoundException, MissingPermissionsException {
		Message message = messageRepository.findByChannelAndId(channel, id);
		String content = body.getContent();

		if (message == null) throw new MessageNotFoundException();
		if (!Objects.equals(message.getAuthor().getUser().getUsername(), member.getUser().getUsername()))
			throw new MissingPermissionsException();

		message.setContent(content);
		messageRepository.save(message);
		log.info("MESSAGE ({}) in CHANNEL ({}) patched successfully", id, channel.getId());

		return message;
	}
}
