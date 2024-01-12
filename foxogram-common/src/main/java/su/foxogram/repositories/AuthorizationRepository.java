package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import su.foxogram.constructors.Authorization;
import su.foxogram.constructors.Session;

import java.util.List;

public interface AuthorizationRepository extends CrudRepository<Authorization, String> {

	@AllowFiltering
	Authorization findById(long id);

	@AllowFiltering
	List<Authorization> findAllById(long id);

	@AllowFiltering
	List<Authorization> findAllBy();

	@AllowFiltering
	Authorization findByAccessToken(String accessToken);

	@AllowFiltering
	List<Authorization> findAllByAccessToken(String accessToken);

	@Override
	void delete(Authorization authorization);
}
