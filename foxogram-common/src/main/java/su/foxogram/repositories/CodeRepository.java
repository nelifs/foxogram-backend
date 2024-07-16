package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.constructors.Code;

import java.util.List;

@Repository
public interface CodeRepository extends CrudRepository<Code, Long> {

	@AllowFiltering
	Code findByUserId(long userId);

	@AllowFiltering
	List<Code> findAllByUserId(long userId);

	@AllowFiltering
	List<Code> findAllBy();

	@AllowFiltering
	Code findByValue(String value);

	@AllowFiltering
	List<Code> findAllByValue(String value);

	@AllowFiltering
	Code findByType(boolean type);

	@AllowFiltering
	List<Code> findAllByType(boolean type);

	@Override
	void delete(Code code);
}
