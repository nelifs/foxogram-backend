package su.foxogram.repositories.cassandra;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Session;

import java.util.List;

@Repository
@RedisHash("sessions")
public interface SessionRepository extends CrudRepository<Session, Long> {

	Session findById(long id);

	List<Session> findAllById(long id);

	List<Session> findAllBy();

	Session findByAccessToken(String accessToken);

	List<Session> findAllByAccessToken(String accessToken);

	@Override
	void delete(@NotNull Session session);
}
