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
import su.foxogram.services.JwtService;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    final AuthenticationService authenticationService;
    final JwtService jwtService;

    @Autowired
    public AuthenticationInterceptor(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws UserUnauthorizedException, UserEmailNotVerifiedException {
        boolean checkIfEmailVerified = request.getRequestURI().contains("email/verify") || request.getRequestURI().contains("users/@me");        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (accessToken == null || !accessToken.contains("Bearer")) throw new UserUnauthorizedException();

        User user = authenticationService.getUser(accessToken, checkIfEmailVerified);

        request.setAttribute("user", user);
        request.setAttribute("accessToken", accessToken);
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) {

    }
}