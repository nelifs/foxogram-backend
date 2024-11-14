package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Member;

import java.util.List;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {
	@Query(value = "SELECT m FROM Member m WHERE m.channelId = :chId AND m.id = :id")
	Member findByChannelIdAndId(@Param("chId") long channelId, @Param("id") long id);

	
	Member findByAccessToken(String accessToken);

	
	List<Member> findAllByAccessToken(String accessToken);

	
	Member findByChannelId(long channelId);

	
	List<Member> findAllByChannelId(long channelId);

	@Override
	void delete(@NotNull Member member);
}
