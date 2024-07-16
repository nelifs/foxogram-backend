package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.constructors.Channel;
import su.foxogram.constructors.Message;
import su.foxogram.constructors.User;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {


	@AllowFiltering
	@Query("SELECT * FROM messages WHERE channelid = ?0 AND timestamp > ?1 LIMIT ?2 ALLOW FILTERING")
	List<Message> findAll(long channelId, long timestamp, int limit);

	@AllowFiltering
	Message findByContent(String content);

	@AllowFiltering
	List<Message> findAllByContent(String content);

	@AllowFiltering
	Message findByChannelId(String channelId);

	@AllowFiltering
	List<Message> findAllByChannelId(String channelId);

	@AllowFiltering
	@Query("SELECT * FROM messages WHERE channelid = ?0 AND id = ?1 ALLOW FILTERING")
	Message findByChannelIdAndId(long channelId, long id);

	@Override
	void delete(Message message);
}
