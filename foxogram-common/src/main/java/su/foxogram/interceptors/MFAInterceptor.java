package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import su.foxogram.constants.UserConstants;
import su.foxogram.exceptions.MFAIsInvalidException;
import su.foxogram.exceptions.TOTPKeyIsInvalidException;
import su.foxogram.models.User;
import su.foxogram.util.Totp;

@Component
public class MFAInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws TOTPKeyIsInvalidException, MFAIsInvalidException {
		User user = (User) request.getAttribute("user");

		if (!user.hasFlag(UserConstants.Flags.MFA_ENABLED) || user.hasFlag(UserConstants.Flags.AWAITING_CONFIRMATION)) return true;

		String code = request.getHeader("code");
		boolean MFAVerified = Totp.validate(user.getKey(), code);

        request.setAttribute("MFAVerified", MFAVerified);

        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception exception) {

    }
}
