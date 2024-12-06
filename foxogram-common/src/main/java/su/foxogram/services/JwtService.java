package su.foxogram.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su.foxogram.configs.JwtConfig;
import su.foxogram.constants.TokenConstants;
import su.foxogram.constants.UserConstants;
import su.foxogram.exceptions.UserEmailNotVerifiedException;
import su.foxogram.exceptions.UserUnauthorizedException;
import su.foxogram.models.User;
import su.foxogram.repositories.UserRepository;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
	private final UserRepository userRepository;
	private final JwtConfig jwtConfig;

	@Autowired
	public JwtService(UserRepository userRepository, JwtConfig jwtConfig) {
		this.userRepository = userRepository;
		this.jwtConfig = jwtConfig;
	}

	public User getUser(String header, boolean ignoreEmailVerification) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		return validateUser(header.substring(7), ignoreEmailVerification);
	}

	public User validateUser(String token, boolean ignoreEmailVerification) throws UserUnauthorizedException, UserEmailNotVerifiedException {
		String userId = validate(token).getId();
		User user = userRepository.findById(userId).get();

		if (!ignoreEmailVerification && user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED))
			throw new UserEmailNotVerifiedException();

		return userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);
	}

	public String generate(String id) {
		long now = System.currentTimeMillis();
		Date expirationDate = new Date(now + TokenConstants.LIFETIME);

		return Jwts.builder()
				.setId(id)
				.setExpiration(expirationDate)
				.signWith(getSigningKey())
				.compact();
	}

	public Claims validate(String token) throws UserUnauthorizedException {
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token);

			return claimsJws.getBody();
		} catch (Exception e) {
			throw new UserUnauthorizedException();
		}
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
