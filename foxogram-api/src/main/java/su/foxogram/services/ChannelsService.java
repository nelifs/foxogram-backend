package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.constants.MemberConstants;
import su.foxogram.exceptions.MemberAlreadyInChannelException;
import su.foxogram.models.*;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.exceptions.MemberInChannelNotFoundException;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.structures.Snowflake;

@Slf4j
@Service
public class ChannelsService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ChannelsService(ChannelRepository channelRepository, MemberRepository memberRepository, AuthenticationService authenticationService) {
        this.channelRepository = channelRepository;
        this.memberRepository = memberRepository;
    }

    public Channel createChannel(User user, int type, String name) {
        long id = new Snowflake(1).nextId();
        long ownerId = user.getId();

        Channel channel = new Channel(id, name, type, ownerId);
        channelRepository.save(channel);

        Member member = new Member(user.getId(), channel, user.getUsername(), MemberConstants.Permissions.ADMIN.getBit(), user.getAvatar(), user.getCreatedAt(), user.getFlags(), user.getType());
        memberRepository.save(member);

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
        Member member = memberRepository.findByChannelAndId(channel, user.getId());

        if (member.hasPermission(MemberConstants.Permissions.ADMIN)) {
            channelRepository.delete(channel);
        } else {
            throw new MissingPermissionsException();
        }
    }

    public void getMemberInChannel(long id, long channelId) throws MemberInChannelNotFoundException {
        Channel channel = channelRepository.findById(channelId);
        Member member = memberRepository.findByChannelAndId(channel, id);

        if (member == null) {
            throw new MemberInChannelNotFoundException();
        }
    }

    public Member joinUser(Channel channel, User user) throws MemberAlreadyInChannelException {
        Member member = memberRepository.findByChannelAndId(channel, user.getId());

        if (member != null) throw new MemberAlreadyInChannelException();

        member = new Member(user.getId(), channel, user.getUsername(), 0, user.getAvatar(), user.getCreatedAt(), user.getFlags(), user.getType());
        return memberRepository.save(member);
    }

    public void leaveUser(Channel channel, User user) throws MemberInChannelNotFoundException {
        Member member = memberRepository.findByChannelAndId(channel, user.getId());

        if (member == null) throw new MemberInChannelNotFoundException();

        member = memberRepository.findByChannelAndId(channel, user.getId());
        memberRepository.delete(member);
    }
}
