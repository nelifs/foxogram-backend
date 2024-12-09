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
		long userId;

		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(header.substring(7));

			userId = Long.parseLong(claimsJws.getBody().getId());
		} catch (Exception e) {
			throw new UserUnauthorizedException();
		}

		User user = userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);

		if (!ignoreEmailVerification && user.hasFlag(UserConstants.Flags.EMAIL_VERIFIED))
			throw new UserEmailNotVerifiedException();

		return userRepository.findById(userId).orElseThrow(UserUnauthorizedException::new);
	}

	public String generate(long id) {
		long now = System.currentTimeMillis();
		Date expirationDate = new Date(now + TokenConstants.LIFETIME);

		return Jwts.builder()
				.setId(String.valueOf(id))
				.setExpiration(expirationDate)
				.signWith(getSigningKey())
				.compact();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
