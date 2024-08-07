package su.foxogram.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.*;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.exceptions.MessageNotFoundException;
import su.foxogram.payloads.MessagePayload;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MessageRepository;
import su.foxogram.structures.Snowflake;

import java.util.List;

@Service
public class MessagesService {

	private final MessageRepository messageRepository;
	private final ChannelRepository channelRepository;
	private final CassandraTemplate cassandraTemplate;
	Logger logger = LoggerFactory.getLogger(MessagesService.class);

	@Autowired
	public MessagesService(MessageRepository messageRepository, ChannelRepository channelRepository, CassandraTemplate cassandraTemplate) {
		this.messageRepository = messageRepository;
		this.channelRepository = channelRepository;
		this.cassandraTemplate = cassandraTemplate;
	}

	public List<Message> getMessages(long before, int limit, long channelId) throws MessageNotFoundException {
		Query query = Query.query(Criteria.where("channelId").is(channelId)).withAllowFiltering();

		if (before > 0) {
			query = query.and(Criteria.where("timestamp").gt(before));
		}

		if (limit > 0) {
			query = query.limit(limit);
		}

		List<Message> messagesArray = cassandraTemplate.select(query, Message.class);

		if (messagesArray.isEmpty()) {
			throw new MessageNotFoundException();
		}
		logger.info("MESSAGES ({}, {}) in CHANNEL ({}) found successfully", limit, before, channelId);

		return messagesArray;
	}

	public Message getMessage(long id, long channelId) throws MessageNotFoundException, ChannelNotFoundException {
		Channel channel = channelRepository.findById(channelId);

		if (channel == null) {
			throw new ChannelNotFoundException();
		}

		Message message = messageRepository.findByChannelIdAndId(channelId, id);

		if (message == null) {
			throw new MessageNotFoundException();
		}

		logger.info("MESSAGE ({}) in CHANNEL ({}) found successfully", id, channelId);

		return message;
	}

	public Message addMessage(long channelId, User user, MessagePayload body) throws ChannelNotFoundException {
		Channel channel = channelRepository.findById(channelId);

		if (channel == null) {
			throw new ChannelNotFoundException();
		}

		long id = new Snowflake(1).nextId();
		long authorId = user.getId();
		long timestamp = System.currentTimeMillis();
		List<String> attachments = body.getAttachments();
		String content = body.getContent();

		Message message = new Message(id, channelId, content, authorId, timestamp, attachments);
		messageRepository.save(message);
		logger.info("MESSAGE ({}) to CHANNEL ({}) saved to database successfully", id, channelId);
		logger.info("MESSAGE ({}) in CHANNEL ({}) posted successfully", id, channelId);

		return message;
	}

	public void deleteMessage(long id, long channelId) throws MessageNotFoundException, ChannelNotFoundException {
		Channel channel = channelRepository.findById(channelId);

		if (channel == null) {
			throw new ChannelNotFoundException();
		}

		Message message = messageRepository.findByChannelIdAndId(channelId, id);

		if (message == null) {
			throw new MessageNotFoundException();
		}

		messageRepository.delete(message);
		logger.info("MESSAGE ({}) in CHANNEL ({}) deleted successfully", id, channelId);
	}

	public Message editMessage(long id, long channelId, MessagePayload body) throws MessageNotFoundException, ChannelNotFoundException {
		Channel channel = channelRepository.findById(channelId);

		if (channel == null) {
			throw new ChannelNotFoundException();
		}

		Message message = messageRepository.findByChannelIdAndId(channelId, id);
		String content = body.getContent();

		if (message == null) {
			throw new MessageNotFoundException();
		}

		message.setContent(content);
		messageRepository.save(message);
		logger.info("MESSAGE ({}) in CHANNEL ({}) patched successfully", id, channelId);

		return message;
	}
}
