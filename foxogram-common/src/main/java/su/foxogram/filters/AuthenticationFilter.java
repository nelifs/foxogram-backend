package su.foxogram.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.models.User;
import su.foxogram.services.JwtService;

import java.io.IOException;
import java.util.Set;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
//	private static final Set<String> MFA_REQUIRED_PATHS = Set.of(
//			"/users/@me/delete/confirm"
//	);

	private static final Set<String> EMAIL_VERIFICATION_IGNORE_PATHS = Set.of(
			"/auth/email/verify",
			"/users/@me",
			"/auth/email/resend"
	);

	final JwtService jwtService;

	public AuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
//		boolean MFAValidationRequired = MFA_REQUIRED_PATHS.stream().anyMatch(requestURI::contains);
		boolean ignoreEmailVerification = EMAIL_VERIFICATION_IGNORE_PATHS.stream().anyMatch(requestURI::contains);

		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (accessToken == null || !accessToken.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		User user;

		try {
			user = jwtService.getUser(accessToken, ignoreEmailVerification);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
			return;
		}

//		if (MFAValidationRequired && user.hasFlag(UserConstants.Flags.AWAITING_CONFIRMATION)) {
//			validateMFA(user, request);
//		}

		request.setAttribute(AttributesConstants.USER, user);
		request.setAttribute(AttributesConstants.ACCESS_TOKEN, accessToken);

		filterChain.doFilter(request, response);
	}

//	private void validateMFA(User user, HttpServletRequest request) throws MFAIsInvalidException, CodeExpiredException, CodeIsInvalidException {
//		String code = request.getHeader("Code");
//
//		if (!user.hasFlag(UserConstants.Flags.MFA_ENABLED)) {
//			boolean MFAVerified = authenticationService.validateCode(code) != null;
//
//			request.setAttribute(AttributesConstants.MFA_VERIFIED, MFAVerified);
//		} else {
//			boolean MFAVerified = Totp.validate(user.getKey(), code);
//
//			request.setAttribute(AttributesConstants.MFA_VERIFIED, MFAVerified);
//		}
//	}
}
