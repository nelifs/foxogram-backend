package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Member;

import java.util.List;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {

	@AllowFiltering
	@Query("SELECT * FROM members WHERE channelid = ?0 AND id = ?1 ALLOW FILTERING")
	Member findByChannelIdAndId(long channelId, long id);

	@AllowFiltering
	Member findByAccessToken(String accessToken);

	@AllowFiltering
	List<Member> findAllByAccessToken(String accessToken);

	@AllowFiltering
	Member findByChannelId(String channelId);

	@AllowFiltering
	List<Member> findAllByChannelId(String channelId);

	@Override
	void delete(@NotNull Member member);
}
