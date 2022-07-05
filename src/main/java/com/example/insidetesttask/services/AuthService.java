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

    /**
     * Generates token with the user's name as a subject and 10 min as expiration time
     * @param user Provided user for getting data
     * @throws io.jsonwebtoken.JwtException if the secret is not satisfied to the JWT specification
     * @return Token value as a string
     */
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

    /**
     *Parses claim of the provided token and takes a subject
     * @param token Token uses to be parsed
     * @throws io.jsonwebtoken.JwtException during parsing. For example, if token value is not valid or an expiration time is ended
     * @return Subject value as a string
     */
    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     *Check provided usernames for equality
     * @param jsonName Name from the client message
     * @param tokenClaimName Name extracted from the token
     * @throws NotCompatibleNameWithTokenSubject If the names are not equal
     */
    public void validateNames(String jsonName, String tokenClaimName) {
        if (!tokenClaimName.equals(jsonName)) {
            throw new NotCompatibleNameWithTokenSubject("Not allowed changes for " + jsonName);
        }
    }
}
