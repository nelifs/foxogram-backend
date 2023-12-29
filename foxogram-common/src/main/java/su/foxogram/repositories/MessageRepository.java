package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import su.foxogram.constructors.Channel;
import su.foxogram.constructors.Message;
import su.foxogram.constructors.User;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {

	@AllowFiltering
	Message findByContent(String content);

	@AllowFiltering
	List<Message> findAllByContent(String content);

	@AllowFiltering
	Message findByChannelId(String channelId);

	@AllowFiltering
	List<Message> findAllByChannelId(String channelId);

	@Override
	void delete(Message message);
}
