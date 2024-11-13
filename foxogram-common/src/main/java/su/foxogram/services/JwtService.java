package su.foxogram.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import su.foxogram.enums.TokenEnum;

import java.util.*;

@Service
public class JwtService {

    @Value("jwt.secret")
    private String secretKey;

    public String generate(long id, TokenEnum.Type tokenType, TokenEnum.Lifetime expirationMillis) {
        Date expirationDate = new Date(expirationMillis.getValue());

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("type", tokenType.getValue())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}