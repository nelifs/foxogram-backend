package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Code;

import java.util.List;

@Repository
public interface CodeRepository extends CrudRepository<Code, Long> {
	Code findByUserId(long userId);

	List<Code> findAllByUserId(long userId);

	List<Code> findAllBy();

	Code findByValue(String value);

	List<Code> findAllByValue(String value);

	Code findByType(String type);

	List<Code> findAllByType(String type);

	@Override
	void delete(@NotNull Code code);
}
