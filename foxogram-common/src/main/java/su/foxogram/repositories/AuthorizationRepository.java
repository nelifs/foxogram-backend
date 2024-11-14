package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Authorization;

import java.util.List;

@Repository
public interface AuthorizationRepository extends CrudRepository<Authorization, Long> {
	Authorization findById(long id);

	
	List<Authorization> findAllById(long id);

	
	List<Authorization> findAllBy();

	
	Authorization findByAccessToken(String accessToken);

	
	List<Authorization> findAllByAccessToken(String accessToken);

	@Override
	void delete(@NotNull Authorization authorization);
}
