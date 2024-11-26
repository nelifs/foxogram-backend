package su.foxogram.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;

import java.util.List;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {
	@Query(value = "SELECT m FROM Member m WHERE m.channel = :ch AND m.id = :id")
	Member findByChannelAndId(@Param("ch") Channel channel, @Param("id") long id);

	Member findByChannel(Channel channel);

	List<Member> findAllByChannel(Channel channel);

	@Override
	void delete(@NotNull Member member);
}
