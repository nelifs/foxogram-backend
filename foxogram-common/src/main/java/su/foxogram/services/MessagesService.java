package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.Message;
import su.foxogram.constructors.MessageRequest;
import su.foxogram.constructors.RequestMessage;
import su.foxogram.constructors.User;
import su.foxogram.exceptions.MessageNotFoundException;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.repositories.MessageRepository;
import su.foxogram.structures.Snowflake;

import java.util.List;

@Service
public class MessagesService {

	private final MessageRepository messageRepository;
	private final MemberRepository memberRepository;
	private final AuthorizationService authorizationService;
	private final CassandraTemplate cassandraTemplate;

	@Autowired
	public MessagesService(MessageRepository messageRepository, MemberRepository memberRepository, AuthorizationService authorizationService, CassandraTemplate cassandraTemplate) {
		this.messageRepository = messageRepository;
		this.memberRepository = memberRepository;
		this.authorizationService = authorizationService;
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

		return messagesArray;
	}

	public Message getMessage(long id, long channelId) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelIdAndId(channelId, id);

		if (message == null) {
			throw new MessageNotFoundException();
		}

		return message;
	}

	public Message addMessage(long channelId, User user, MessageRequest body) {
		long id = new Snowflake(1).nextId();
		long authorId = user.getId();
		long timestamp = System.currentTimeMillis();
		List<String> attachments = body.getAttachments();
		String content = body.getContent();

		Message message = new Message(id, channelId, content, authorId, timestamp, attachments);
		return messageRepository.save(message);
	}

	public void deleteMessage(long id, long channelId) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelIdAndId(channelId, id);

		if (message == null) {
			throw new MessageNotFoundException();
		}

		messageRepository.delete(message);
	}

	public Message editMessage(long id, long channelId, MessageRequest body) throws MessageNotFoundException {
		Message message = messageRepository.findByChannelIdAndId(channelId, id);
		String content = body.getContent();

		if (message == null) {
			throw new MessageNotFoundException();
		}

		message.setContent(content);
		return messageRepository.save(message);
	}
}
