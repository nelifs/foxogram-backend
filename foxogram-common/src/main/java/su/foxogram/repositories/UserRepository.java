package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
	@NotNull
	Optional<User> findById(@NotNull String id);

	User findByEmail(String email);

	User findByUsername(String username);

	@Override
	void delete(@NotNull User user);
}
