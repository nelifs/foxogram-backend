package su.foxogram.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import su.foxogram.enums.TokenEnum;
import su.foxogram.util.Env;

import java.security.Key;
import java.util.*;


@Service
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generate(long id, TokenEnum.Type tokenType, TokenEnum.Lifetime expirationMillis) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationMillis.getValue());

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", id);
        claims.put("token_type", tokenType.getValue());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SECRET_KEY)
                .compact();
    }
}