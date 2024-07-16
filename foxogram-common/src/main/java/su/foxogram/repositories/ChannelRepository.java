package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.constructors.Channel;
import su.foxogram.constructors.Member;

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
	void delete(Channel channel);
}
