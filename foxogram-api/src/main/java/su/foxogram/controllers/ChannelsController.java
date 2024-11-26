package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import su.foxogram.dtos.response.ChannelDTO;
import su.foxogram.dtos.response.MemberDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.models.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.exceptions.*;
import su.foxogram.dtos.request.ChannelCreateDTO;
import su.foxogram.services.ChannelsService;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;

	public ChannelsController(ChannelsService channelsService) {
		this.channelsService = channelsService;
    }

	@PostMapping("/create")
	public ChannelDTO createChannel(@RequestAttribute(value = "user") User user, @Valid @RequestBody ChannelCreateDTO body, HttpServletRequest request) {
		log.info("CHANNEL create ({}, {}) request", body.getName(), body.getType());

		Channel channel = channelsService.createChannel(user, body.getType(), body.getName());

		return new ChannelDTO(channel);
	}

	@GetMapping("/{id}")
	public ChannelDTO getChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException {
		log.info("CHANNEL info ({}) request", id);

		Channel channel = channelsService.getChannel(id);

		return new ChannelDTO(channel);
	}

	@PostMapping("/{id}/join")
	public MemberDTO joinChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, MemberAlreadyInChannelException {
		log.info("CHANNEL join ({}) request", id);
		Channel channel = channelsService.getChannel(id);

		Member member = channelsService.joinUser(channel, user);

		return new MemberDTO(member);
	}

	@PostMapping("/{id}/leave")
	public OkDTO leaveChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, MemberInChannelNotFoundException {
		log.info("CHANNEL leave ({}) request", id);
		Channel channel = channelsService.getChannel(id);

		channelsService.leaveUser(channel, user);

		return new OkDTO(true);
	}

	@PatchMapping("/{id}")
	public ChannelDTO editChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException {
		log.info("CHANNEL edit ({}) request", id);

		Channel channel = channelsService.getChannel(id);

		return new ChannelDTO(channel);
	}

	@DeleteMapping("/{id}")
	public OkDTO deleteChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, MissingPermissionsException {
		log.info("CHANNEL delete ({}) request", id);
		Channel channel = channelsService.getChannel(id);

		channelsService.deleteChannel(channel, user);

		return new OkDTO(true);
	}
}
