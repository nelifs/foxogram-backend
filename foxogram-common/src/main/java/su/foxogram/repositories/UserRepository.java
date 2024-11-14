package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.User;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	User findById(long id);
	
	List<User> findAllById(long id);
	
	List<User> findAllBy();
	
	User findByEmail(String email);
	
	List<User> findAllByEmail(String email);
	
	User findByAccessToken(String accessToken);

	List<User> findAllByAccessToken(String accessToken);

	@Override
	void delete(@NotNull User user);
}
