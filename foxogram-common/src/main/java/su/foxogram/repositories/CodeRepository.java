package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Code;

@Repository
public interface CodeRepository extends CrudRepository<Code, String> {
	Code findByUserId(String userId);

	Code findByValue(String value);

	@Override
	void delete(@NotNull Code code);
}
