package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import su.foxogram.constructors.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

	@AllowFiltering
	User findById(long id);

	@AllowFiltering
	List<User> findAllById(long id);

	@AllowFiltering
	List<User> findAllBy();

	@AllowFiltering
	User findByEmail(String email);

	@AllowFiltering
	List<User> findAllByEmail(String email);

	@AllowFiltering
	User findByAccessToken(String accessToken);

	@AllowFiltering
	List<User> findAllByAccessToken(String accessToken);

	@Override
	void delete(User user);
}
