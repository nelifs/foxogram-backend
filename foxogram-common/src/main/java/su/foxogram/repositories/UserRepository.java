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

	User findByUsername(String username);
	
	List<User> findAllByEmail(String email);

	@Override
	void delete(@NotNull User user);
}
