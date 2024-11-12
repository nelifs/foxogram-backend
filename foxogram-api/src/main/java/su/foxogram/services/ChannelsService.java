package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import su.foxogram.models.*;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.exceptions.MemberInChannelNotFoundException;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.utils.PayloadBuilder;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.structures.Snowflake;

@Service
public class ChannelsService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ChannelsService(ChannelRepository channelRepository, MemberRepository memberRepository, AuthenticationService authenticationService, CassandraTemplate cassandraTemplate) {
        this.channelRepository = channelRepository;
        this.memberRepository = memberRepository;
    }

    public Channel createChannel(User user, String type, String name) {
        long id = new Snowflake(1).nextId();
        long ownerId = user.getId();

        return new Channel(id, name, type, ownerId);
    }

    public Channel getChannel(long id) throws ChannelNotFoundException {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        return channel;
    }

    public ResponseEntity<String> deleteChannel(Channel channel, User user) throws MissingPermissionsException {
        Member member = memberRepository.findByChannelIdAndId(channel.getId(), user.getId());
        if (member.isAdmin()) {
            channelRepository.delete(channel);
            return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "Channel has been successfully deleted!").build());
        } else {
            throw new MissingPermissionsException();
        }
    }

    public void getMemberInChannel(long id, long channelId) throws MemberInChannelNotFoundException {
        Member member = memberRepository.findByChannelIdAndId(channelId, id);

        if (member == null) {
            throw new MemberInChannelNotFoundException();
        }
    }

    public Member joinUser(Channel channel, User user) {
        Member member = new Member(user.getId(), channel.getId(), user.getUsername(), user.getAccessToken(), false, user.getAvatar(), user.getCreatedAt(), user.getFlags());
        return memberRepository.save(member);
    }

    public void leaveUser(Channel channel, User user) {
        Member member = memberRepository.findByChannelIdAndId(channel.getId(), user.getId());
        memberRepository.delete(member);
    }
}
