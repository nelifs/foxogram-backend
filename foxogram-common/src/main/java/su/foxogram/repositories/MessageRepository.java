package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Message;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
	@Query("SELECT m FROM Message m WHERE m.channelId = :chId AND m.timestamp > :ts")
	List<Message> findAll(@Param("chId") long channelId, @Param("ts") long timestamp);

	
	Message findByContent(String content);

	
	List<Message> findAllByContent(String content);

	
	Message findByChannelId(long channelId);

	
	List<Message> findAllByChannelId(long channelId);

	
	@Query("SELECT m FROM Message m WHERE m.channelId = :chId AND m.id = :id")
	Message findByChannelIdAndId(@Param("chId") long channelId, @Param("id") long id);

	@Override
	void delete(@NotNull Message message);
}
