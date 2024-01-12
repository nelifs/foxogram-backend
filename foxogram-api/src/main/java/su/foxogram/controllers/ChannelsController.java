package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.foxogram.constructors.Channel;
import su.foxogram.constructors.Member;
import su.foxogram.constructors.User;
import su.foxogram.enums.APIEnum;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.exceptions.UserAuthenticationNeededException;
import su.foxogram.exceptions.UserEmailNotVerifiedException;
import su.foxogram.exceptions.UserNotFoundException;
import su.foxogram.services.AuthorizationService;
import su.foxogram.services.ChannelsService;

@RestController
@RequestMapping(value = APIEnum.CHANNELS, produces = "application/json")
public class ChannelsController {

	private final ChannelsService channelsService;
	private final AuthorizationService authorizationService;
	Logger logger = LoggerFactory.getLogger(ChannelsController.class);

	public ChannelsController(ChannelsService channelsService, AuthorizationService authorizationService) {
		this.channelsService = channelsService;
		this.authorizationService = authorizationService;
	}

	@PostMapping("/{id}/join")
	public Member joinChannel(@PathVariable long id, HttpServletRequest request) throws ChannelNotFoundException, UserNotFoundException, UserAuthenticationNeededException, UserEmailNotVerifiedException {
		logger.info("CHANNEL join ({}) request", id);
		Channel channel = channelsService.getChannel(id);
		User user = authorizationService.getUser(request, true, true);

		return channelsService.joinUser(channel, user);
	}
}
