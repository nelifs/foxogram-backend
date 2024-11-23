package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;
import su.foxogram.models.Message;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
	@Query("SELECT m FROM Message m WHERE m.channel = :ch AND m.timestamp > :ts")
	List<Message> findAll(@Param("ch") Channel channel, @Param("ts") long timestamp);

	
	Message findByContent(String content);

	
	List<Message> findAllByContent(String content);

	
	Message findByChannel(Channel channel);

	
	List<Message> findAllByChannel(Channel channel);

	
	@Query("SELECT m FROM Message m WHERE m.channel = :ch AND m.id = :id")
	Message findByChannelAndId(@Param("ch") Channel channel, @Param("id") long id);

	@Override
	void delete(@NotNull Message message);
}
