package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.constructors.Session;
import su.foxogram.constructors.User;

import java.util.List;

@Repository
public interface SessionRepository extends CrudRepository<Session, String> {

	@AllowFiltering
	Session findById(long id);

	@AllowFiltering
	List<Session> findAllById(long id);

	@AllowFiltering
	List<Session> findAllBy();

	@AllowFiltering
	Session findByAccessToken(String accessToken);

	@AllowFiltering
	List<Session> findAllByAccessToken(String accessToken);

	@Override
	void delete(Session session);
}
