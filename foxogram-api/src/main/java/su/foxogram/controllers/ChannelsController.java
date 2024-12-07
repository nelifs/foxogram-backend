package su.foxogram.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
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
import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "Channels")
@RequestMapping(value = APIConstants.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;

	public ChannelsController(ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

	@Operation(summary = "Create channel")
	@PostMapping("/")
	public ChannelDTO createChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @Valid @RequestBody ChannelCreateDTO body) {
		Channel channel = channelsService.createChannel(user, body.getType(), body.getName());

		return new ChannelDTO(channel);
	}

	@Operation(summary = "Get channel")
	@GetMapping("/{id}")
	public ChannelDTO getChannel(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) {
		return new ChannelDTO(channel);
	}

	@Operation(summary = "Edit channel")
	@PatchMapping("/{id}")
	public ChannelDTO editChannel(@RequestAttribute(value = AttributesConstants.MEMBER) Member member, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @Valid @RequestBody ChannelEditDTO body) throws MissingPermissionsException {
		channel = channelsService.editChannel(member, channel, body);

		return new ChannelDTO(channel);
	}

	@Operation(summary = "Delete channel")
	@DeleteMapping("/{id}")
	public OkDTO deleteChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MissingPermissionsException {
		channelsService.deleteChannel(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Join channel")
	@PutMapping("/{id}/members/@me")
	public MemberDTO joinChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable long id) throws MemberAlreadyInChannelException {
		Member member = channelsService.joinUser(channel, user);

		return new MemberDTO(member);
	}

	@Operation(summary = "Leave channel")
	@DeleteMapping("/{id}/members/@me")
	public OkDTO leaveChannel(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) throws MemberInChannelNotFoundException {
		channelsService.leaveUser(channel, user);

		return new OkDTO(true);
	}

	@Operation(summary = "Get member")
	@GetMapping("/{id}/members/{memberId}")
	public MemberDTO getMember(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel, @PathVariable String memberId) throws MemberInChannelNotFoundException {
		if (Objects.equals(memberId, "@me")) {
			memberId = user.getId();
		}

		Member member = channelsService.getMember(channel, memberId);

		if (member == null) throw new MemberInChannelNotFoundException();

		return new MemberDTO(member);
	}

	@Operation(summary = "Get members")
	@GetMapping("/{id}/members")
	public List<MemberDTO> getMembers(@RequestAttribute(value = AttributesConstants.CHANNEL) Channel channel) {
		return channelsService.getMembers(channel);
	}
}
