package su.foxogram.repositories.cassandra;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;

import java.util.List;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {

	@AllowFiltering
	Channel findById(long id);

	@AllowFiltering
	Member findByName(String name);

	@AllowFiltering
	List<Member> findAllByName(String name);

	@Override
	void delete(@NotNull Channel channel);
}
