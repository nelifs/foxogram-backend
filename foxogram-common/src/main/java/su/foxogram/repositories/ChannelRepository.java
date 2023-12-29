package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import su.foxogram.constructors.Channel;
import su.foxogram.constructors.Member;

import java.util.List;

public interface ChannelRepository extends CrudRepository<Channel, Long> {

	@AllowFiltering
	Member findByName(String name);

	@AllowFiltering
	List<Member> findAllByName(String name);

	@Override
	void delete(Channel channel);
}
