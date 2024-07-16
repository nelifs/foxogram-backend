package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constructors.*;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.*;
import su.foxogram.services.AuthenticationService;
import su.foxogram.services.ChannelsService;

@RestController
@RequestMapping(value = APIEnum.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;
	private final AuthenticationService authenticationService;
	Logger logger = LoggerFactory.getLogger(ChannelsController.class);

	public ChannelsController(ChannelsService channelsService, AuthenticationService authenticationService) {
		this.channelsService = channelsService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/create")
	public Channel createChannel(@RequestBody ChannelCreateRequest body, HttpServletRequest request) throws UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL create ({}) request");
		User user = authenticationService.getUser(request, true, true);

		return channelsService.createChannel(user, body.getType(), body.getName());
	}

	@GetMapping("/{id}")
	public Channel getChannel(@PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL info ({}) request", id);
		authenticationService.getUser(request, true, true);

		return channelsService.getChannel(id);
	}

	@PostMapping("/{id}/join")
	public Member joinChannel(@PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL join ({}) request", id);
		Channel channel = channelsService.getChannel(id);
		User user = authenticationService.getUser(request, true, true);

		return channelsService.joinUser(channel, user);
	}

	@PostMapping("/{id}/leave")
	public ResponseEntity<String> leaveChannel(@PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL leave ({}) request", id);
		Channel channel = channelsService.getChannel(id);
		User user = authenticationService.getUser(request, true, true);

		channelsService.leaveUser(channel, user);
		return ResponseEntity.ok(new RequestMessage().setSuccess(true).addField("message", "You have successfully left the channel!").build());
	}

	@PatchMapping("/{id}")
	public Channel editChannel(@PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL edit ({}) request", id);
		authenticationService.getUser(request, true, true);

		return channelsService.getChannel(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteChannel(@PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException, MissingPermissionsException {
		logger.info("CHANNEL delete ({}) request", id);
		Channel channel = channelsService.getChannel(id);
		User user = authenticationService.getUser(request, true, true);

		return channelsService.deleteChannel(channel, user);
	}
}
