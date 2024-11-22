package su.foxogram.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.models.*;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.exceptions.MemberInChannelNotFoundException;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.structures.Snowflake;

@Service
public class ChannelsService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ChannelsService(ChannelRepository channelRepository, MemberRepository memberRepository, AuthenticationService authenticationService) {
        this.channelRepository = channelRepository;
        this.memberRepository = memberRepository;
    }

    public Channel createChannel(User user, String type, String name) {
        long id = new Snowflake(1).nextId();
        long ownerId = user.getId();

        Channel channel = new Channel(id, name, type, ownerId);
        channelRepository.save(channel);

        return channel;
    }

    public Channel getChannel(long id) throws ChannelNotFoundException {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new ChannelNotFoundException();
        }

        return channel;
    }

    public void deleteChannel(Channel channel, User user) throws MissingPermissionsException {
        Member member = memberRepository.findByChannelIdAndId(channel.getId(), user.getId());
        if (member.isAdmin()) {
            channelRepository.delete(channel);
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
        Member member = new Member(user.getId(), channel.getId(), user.getUsername(), false, user.getAvatar(), user.getCreatedAt(), user.getFlags(), user.getType());
        return memberRepository.save(member);
    }

    public void leaveUser(Channel channel, User user) {
        Member member = memberRepository.findByChannelIdAndId(channel.getId(), user.getId());
        memberRepository.delete(member);
    }
}
