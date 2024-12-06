package su.foxogram.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.exceptions.*;
import su.foxogram.models.User;
import su.foxogram.services.AuthenticationService;
import su.foxogram.util.Totp;

import java.util.Set;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	private static final Set<String> MFA_REQUIRED_PATHS = Set.of(
			"/v1/users/@me/delete/confirm"
	);

	private static final Set<String> EMAIL_VERIFICATION_IGNORE_PATHS = Set.of(
			"/v1/auth/email/verify",
			"/v1/users/@me",
			"/v1/auth/email/resend"
	);

	final AuthenticationService authenticationService;

	@Autowired
	public AuthenticationInterceptor(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws UserUnauthorizedException, UserEmailNotVerifiedException, MFAIsInvalidException, CodeExpiredException, CodeIsInvalidException {
		String requestURI = request.getRequestURI();
		boolean MFAValidationRequired = MFA_REQUIRED_PATHS.stream().anyMatch(requestURI::contains);
		boolean ignoreEmailVerification = EMAIL_VERIFICATION_IGNORE_PATHS.stream().anyMatch(requestURI::contains);

		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (accessToken == null || !accessToken.startsWith("Bearer "))
			throw new UserUnauthorizedException();

		User user = authenticationService.getUser(accessToken, ignoreEmailVerification);

		if (MFAValidationRequired && user.hasFlag(UserConstants.Flags.AWAITING_CONFIRMATION)) {
			validateMFA(user, request);
		}

		request.setAttribute(AttributesConstants.USER, user);
		request.setAttribute(AttributesConstants.ACCESS_TOKEN, accessToken);

		return true;
	}

	private void validateMFA(User user, HttpServletRequest request) throws MFAIsInvalidException, CodeExpiredException, CodeIsInvalidException {
		String code = request.getHeader("Code");

		if (!user.hasFlag(UserConstants.Flags.MFA_ENABLED)) {
			boolean MFAVerified = authenticationService.validateCode(code) != null;

			request.setAttribute(AttributesConstants.MFA_VERIFIED, MFAVerified);
		} else {
			boolean MFAVerified = Totp.validate(user.getKey(), code);

			request.setAttribute(AttributesConstants.MFA_VERIFIED, MFAVerified);
		}
	}
}
