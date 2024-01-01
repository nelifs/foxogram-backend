package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import su.foxogram.constructors.*;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.exceptions.MemberInChannelNotFoundException;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MemberRepository;

@Service
public class ChannelsService {

	private final ChannelRepository channelRepository;
	private final MemberRepository memberRepository;
	private final CassandraTemplate cassandraTemplate;

	@Autowired
	public ChannelsService(ChannelRepository channelRepository, MemberRepository memberRepository, AuthorizationService authorizationService, CassandraTemplate cassandraTemplate) {
		this.channelRepository = channelRepository;
		this.memberRepository = memberRepository;
		this.cassandraTemplate = cassandraTemplate;
	}

	public Channel getChannel(long id) throws ChannelNotFoundException {
		Channel channel = channelRepository.findById(id);

		if (channel == null) {
			throw new ChannelNotFoundException();
		}

		return channel;
	}

	public void getMemberInChannel(long id, long channelId) throws MemberInChannelNotFoundException {
		Member member = memberRepository.findByChannelIdAndId(channelId, id);

		if (member == null) {
			throw new MemberInChannelNotFoundException();
		}
	}

	public Member joinUser(Channel channel, User user) {
		Member member = new Member(user.getId(), channel.getId(), user.getUsername(), user.getAccessToken(), user.getAvatar(), user.getCreatedAt(), user.getFlags());
		return memberRepository.save(member);
	}
}
