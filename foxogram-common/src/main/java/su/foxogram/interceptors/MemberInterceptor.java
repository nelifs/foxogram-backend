package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.exceptions.MemberInChannelNotFoundException;
import su.foxogram.models.Channel;
import su.foxogram.models.Member;
import su.foxogram.models.User;
import su.foxogram.repositories.MemberRepository;
import su.foxogram.services.ChannelsService;

import java.util.Map;
import java.util.TreeMap;

@Component
public class MemberInterceptor implements HandlerInterceptor {

    private final MemberRepository memberRepository;
    private final ChannelsService channelsService;

    @Autowired
    public MemberInterceptor(MemberRepository memberRepository, ChannelsService channelsService) {
        this.memberRepository = memberRepository;
        this.channelsService = channelsService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws MemberInChannelNotFoundException, ChannelNotFoundException {
        User user = (User) request.getAttribute("user");
        Channel channel = (Channel) request.getAttribute("channel");

        Member member = channelsService.getMember(channel, user.getId());

        if (member == null) throw new ChannelNotFoundException();

        request.setAttribute("member", member);
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) {

    }
}