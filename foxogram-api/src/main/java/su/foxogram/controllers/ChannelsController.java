package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.models.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.*;
import su.foxogram.dtos.ChannelCreateDTO;
import su.foxogram.utils.PayloadBuilder;
import su.foxogram.services.AuthenticationService;
import su.foxogram.services.ChannelsService;

@RestController
@RequestMapping(value = APIEnum.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;
	private final AuthenticationService authenticationService;
	final Logger logger = LoggerFactory.getLogger(ChannelsController.class);

	public ChannelsController(ChannelsService channelsService, AuthenticationService authenticationService) {
		this.channelsService = channelsService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/create")
	public Channel createChannel(@RequestAttribute(value = "user") User user, @RequestBody ChannelCreateDTO body, HttpServletRequest request) throws UserUnauthorizedException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL create ({}) request");

		return channelsService.createChannel(user, body.getType(), body.getName());
	}

	@GetMapping("/{id}")
	public Channel getChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserUnauthorizedException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL info ({}) request", id);

		return channelsService.getChannel(id);
	}

	@PostMapping("/{id}/join")
	public Member joinChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserUnauthorizedException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL join ({}) request", id);
		Channel channel = channelsService.getChannel(id);

		return channelsService.joinUser(channel, user);
	}

	@PostMapping("/{id}/leave")
	public ResponseEntity<String> leaveChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserUnauthorizedException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL leave ({}) request", id);
		Channel channel = channelsService.getChannel(id);

		channelsService.leaveUser(channel, user);
		return ResponseEntity.ok(new PayloadBuilder().setSuccess(true).addField("message", "You have successfully left the channel!").build());
	}

	@PatchMapping("/{id}")
	public Channel editChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserUnauthorizedException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL edit ({}) request", id);

		return channelsService.getChannel(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteChannel(@RequestAttribute(value = "user") User user, @PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserUnauthorizedException, UserAuthenticationNeededException, UserEmailNotVerifiedException, MissingPermissionsException {
		logger.info("CHANNEL delete ({}) request", id);
		Channel channel = channelsService.getChannel(id);

		return channelsService.deleteChannel(channel, user);
	}
}
