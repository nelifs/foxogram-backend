package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import su.foxogram.exceptions.UserEmailNotVerifiedException;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws UserUnauthorizedException, UserEmailNotVerifiedException {
        User user = authenticationService.getUser(request.getHeader(HttpHeaders.AUTHORIZATION));
        request.setAttribute("user", user);
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) {

    }
}