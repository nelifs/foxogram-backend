package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Authorization;

import java.util.List;

@Repository
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
	void delete(@NotNull Authorization authorization);
}
