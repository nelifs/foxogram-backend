package su.foxogram.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.dtos.request.ChannelCreateDTO;
import su.foxogram.dtos.request.ChannelEditDTO;
import su.foxogram.dtos.response.ChannelDTO;
import su.foxogram.dtos.response.MemberDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.exceptions.MemberAlreadyInChannelException;
import su.foxogram.exceptions.MemberInChannelNotFoundException;
import su.foxogram.exceptions.MissingPermissionsException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.services.ChannelsService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;

	public ChannelsController(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

	@PostMapping("/")
	public ChannelDTO createChannel(@RequestAttribute(value = "user") User user, @Valid @RequestBody ChannelCreateDTO body) {
		log.info("CHANNEL create ({}, {}) request", body.getName(), body.getType());

		Channel channel = channelsService.createChannel(user, body.getType(), body.getName());

		return new ChannelDTO(channel);
	}

	@GetMapping("/{id}")
	public ChannelDTO getChannel(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel) {
		log.info("CHANNEL info ({}) request", channel.getId());

		return new ChannelDTO(channel);
	}

	@DeleteMapping("/{id}/join")
	public MemberDTO joinChannel(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel, @PathVariable long id) throws MemberAlreadyInChannelException {
		log.info("CHANNEL join ({}) request", channel.getId());

		Member member = channelsService.joinUser(channel, user);

		return new MemberDTO(member);
	}

	@PostMapping("/{id}/leave")
	public OkDTO leaveChannel(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel) throws MemberInChannelNotFoundException {
		log.info("CHANNEL leave ({}) request", channel.getId());

		channelsService.leaveUser(channel, user);

		return new OkDTO(true);
	}

	@PatchMapping("/{id}")
	public ChannelDTO editChannel(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "member") Member member, @RequestAttribute(value = "channel") Channel channel, @Valid @RequestBody ChannelEditDTO body) throws MissingPermissionsException {
		log.info("CHANNEL edit ({}) request", channel.getId());

		channel = channelsService.editChannel(member, channel, body);

		return new ChannelDTO(channel);
	}

	@DeleteMapping("/{id}")
	public OkDTO deleteChannel(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel) throws MissingPermissionsException {
		log.info("CHANNEL delete ({}) request", channel.getId());

		channelsService.deleteChannel(channel, user);

		return new OkDTO(true);
	}

	@GetMapping("/{id}/members")
	public List<MemberDTO> getMembers(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel) {
		log.info("CHANNEL get members ({}) request", channel.getId());

		return channelsService.getMembers(channel);
	}

	@GetMapping("/{id}/members/{memberId}")
	public MemberDTO getMember(@RequestAttribute(value = "user") User user, @RequestAttribute(value = "channel") Channel channel, @PathVariable String memberId) throws MemberInChannelNotFoundException {
		log.info("CHANNEL get member ({}, {}) request", channel.getId(), memberId);

		Member member = channelsService.getMember(channel, memberId);

		if (member == null) throw new MemberInChannelNotFoundException();

		return new MemberDTO(member);
	}
}
