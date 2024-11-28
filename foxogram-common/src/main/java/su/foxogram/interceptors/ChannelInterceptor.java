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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class ChannelInterceptor implements HandlerInterceptor {

    private final ChannelsService channelsService;

    @Autowired
    public ChannelInterceptor(ChannelsService channelsService) {
        this.channelsService = channelsService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ChannelNotFoundException {
        Map<String, String> uriVariables = (Map<String, String>) getUriVariables(request);

        long channelId = getChannelId(uriVariables).orElseThrow(ChannelNotFoundException::new);

        request.setAttribute("channel", channelsService.getChannel(channelId));

        return true;
    }

    private Map<?, ?> getUriVariables(HttpServletRequest request) {
        return Optional.ofNullable(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .orElseGet(Collections::emptyMap);
    }

    private Optional<Long> getChannelId(Map<String, String> uriVariables) {
        String channelIdString = uriVariables.get("id");

        try {
            return Optional.ofNullable(channelIdString)
                    .map(Long::parseLong);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) {

    }
}