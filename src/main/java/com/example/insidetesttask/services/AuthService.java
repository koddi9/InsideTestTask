package com.example.insidetesttask.services;

import com.example.insidetesttask.exceptions.NotCompatibleNameWithTokenSubject;
import com.example.insidetesttask.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class AuthService {

    SecretKey jwtSecret;

    public AuthService(@Value("${jwt.secret}") String secret) {
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now.plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getName())
                .setExpiration(accessExpiration)
                .signWith(jwtSecret)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public void validateNames(String jsonName, String tokenClaimName) {
        if (!tokenClaimName.equals(jsonName)) {
            throw new NotCompatibleNameWithTokenSubject("Not allowed changes for " + jsonName);
        }
    }
}
