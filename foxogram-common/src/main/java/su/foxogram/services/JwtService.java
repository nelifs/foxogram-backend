package su.foxogram.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import su.foxogram.enums.TokenEnum;
import su.foxogram.util.Env;

import java.util.*;

@Service
public class JwtService {

    private static final String SECRET_KEY = Env.get("JWT_SECRET_KEY");

    public static String generate(long id, TokenEnum.Type tokenType, TokenEnum.Lifetime expirationMillis) {
        Date expirationDate = new Date(expirationMillis.getValue());

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("type", tokenType.getValue())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    public static boolean validate(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}