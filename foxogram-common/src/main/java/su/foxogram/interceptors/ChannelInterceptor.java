package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import su.foxogram.exceptions.ChannelNotFoundException;
import su.foxogram.services.ChannelsService;

import java.util.Map;
import java.util.TreeMap;

@Component
public class ChannelInterceptor implements HandlerInterceptor {

    private final ChannelsService channelsService;

    @Autowired
    public ChannelInterceptor(ChannelsService channelsService) {
        this.channelsService = channelsService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ChannelNotFoundException {
        Map<String, String> map = new TreeMap<>((Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
        long id = Long.parseLong(map.get("id").toString());

        request.setAttribute("channel", channelsService.getChannel(id));
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) {

    }
}