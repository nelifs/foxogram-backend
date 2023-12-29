package su.foxogram.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import su.foxogram.constructors.Member;

import java.util.List;

public interface MemberRepository extends CrudRepository<Member, Long> {

	@AllowFiltering
	Member findByAccessToken(String accessToken);

	@AllowFiltering
	List<Member> findAllByAccessToken(String accessToken);

	@AllowFiltering
	Member findByChannelId(String channelId);

	@AllowFiltering
	List<Member> findAllByChannelId(String channelId);

	@Override
	void delete(Member member);
}
