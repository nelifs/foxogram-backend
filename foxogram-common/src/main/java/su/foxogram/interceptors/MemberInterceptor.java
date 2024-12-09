package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.services.ChannelsService;

@Component
public class MemberInterceptor implements HandlerInterceptor {

	private final ChannelsService channelsService;

	@Autowired
	public MemberInterceptor(MemberRepository memberRepository, ChannelsService channelsService) {
		this.channelsService = channelsService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ChannelNotFoundException {
		User user = (User) request.getAttribute(AttributesConstants.USER);
		Channel channel = (Channel) request.getAttribute(AttributesConstants.CHANNEL);

		Member member = channelsService.getMember(channel, user.getUsername());

		request.setAttribute("member", member);
		return true;
	}
}
