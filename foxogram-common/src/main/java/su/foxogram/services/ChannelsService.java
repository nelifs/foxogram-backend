package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.foxogram.constants.MemberConstants;
import su.foxogram.dtos.request.ChannelCreateDTO;
import su.foxogram.dtos.request.ChannelEditDTO;
import su.foxogram.dtos.response.MemberDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.repositories.ChannelRepository;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.repositories.UserRepository;

import java.util.List;

@Slf4j
@Service
public class ChannelsService {
	private final ChannelRepository channelRepository;

	private final MemberRepository memberRepository;

	private final UserRepository userRepository;

	@Autowired
	public ChannelsService(ChannelRepository channelRepository, MemberRepository memberRepository, UserRepository userRepository) {
		this.channelRepository = channelRepository;
		this.memberRepository = memberRepository;
		this.userRepository = userRepository;
	}

	public Channel createChannel(User user, ChannelCreateDTO body) throws ChannelAlreadyExistException {
		String owner = user.getUsername();
		Channel channel;

		try {
			channel = new Channel(0, body.getDisplayName(), body.getName(), body.getType(), owner);
			channelRepository.save(channel);
		} catch (DataIntegrityViolationException e) {
			throw new ChannelAlreadyExistException();
		}

		user = userRepository.findByUsername(owner);

		Member member = new Member(user, channel, MemberConstants.Permissions.ADMIN.getBit());
		memberRepository.save(member);

		return channel;
	}

	public Channel getChannel(String name) throws ChannelNotFoundException {
		Channel channel = channelRepository.findByName(name);

		if (channel == null) throw new ChannelNotFoundException();

		return channel;
	}

	public Channel editChannel(Member member, Channel channel, ChannelEditDTO body) throws MissingPermissionsException, UserCredentialsDuplicateException {
		member.hasAnyPermission(MemberConstants.Permissions.ADMIN, MemberConstants.Permissions.MANAGE_CHANNEL);

		try {
			if (body.getDisplayName() != null) channel.setDisplayName(body.getDisplayName());
			if (body.getName() != null) channel.setName(body.getName());

			channelRepository.save(channel);
		} catch (DataIntegrityViolationException e) {
			throw new UserCredentialsDuplicateException();
		}

		return channel;
	}

	public void deleteChannel(Channel channel, User user) throws MissingPermissionsException {
		Member member = memberRepository.findByChannelAndUser(channel, user);

		member.hasAnyPermission(MemberConstants.Permissions.ADMIN);

		channelRepository.delete(channel);
	}

	public Member joinUser(Channel channel, User user) throws MemberAlreadyInChannelException {
		Member member = memberRepository.findByChannelAndUsername(channel, user.getUsername());

		if (member != null) throw new MemberAlreadyInChannelException();

		user = userRepository.findByUsername(user.getUsername());

		member = new Member(user, channel, 0);
		return memberRepository.save(member);
	}

	public void leaveUser(Channel channel, User user) throws MemberInChannelNotFoundException {
		Member member = memberRepository.findByChannelAndUser(channel, user);

		if (member == null) throw new MemberInChannelNotFoundException();

		member = memberRepository.findByChannelAndUser(channel, user);
		memberRepository.delete(member);
	}

	public List<MemberDTO> getMembers(Channel channel) {
		return memberRepository.findAllByChannel(channel).stream()
				.map(MemberDTO::new)
				.toList();
	}

	public Member getMember(Channel channel, String memberUsername) {
		return memberRepository.findByChannelAndUsername(channel, memberUsername);
	}
}
