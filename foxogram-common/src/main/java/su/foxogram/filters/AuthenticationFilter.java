package su.foxogram.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.models.User;
import su.foxogram.services.JwtService;

import java.io.IOException;
import java.util.Set;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
	private static final Set<String> EMAIL_VERIFICATION_IGNORE_PATHS = Set.of(
			"/auth/email/verify",
			"/users/@me",
			"/auth/email/resend"
	);

	private final JwtService jwtService;

	private final UserDetailsService userDetailsService;

	public AuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
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

		request.setAttribute(AttributesConstants.USER, user);
		request.setAttribute(AttributesConstants.ACCESS_TOKEN, accessToken);

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		filterChain.doFilter(request, response);
	}
}
