package su.foxogram.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import su.foxogram.configs.JwtConfig;
import su.foxogram.constants.TokenConstants;
import su.foxogram.exceptions.UserUnauthorizedException;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
	private final JwtConfig jwtConfig;

	public JwtService(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
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
