package com.example.insidetesttask.services;

import com.example.insidetesttask.models.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
public class AuthServiceTests {

    @Autowired
    AuthService service;

    @Value("${app.test.token}")
    String token ;

    @Test
    public void givenValidUser_whenGenerateToken_returnsValidStringToken() {
        User user = new User();
        user.setName("name");
        user.setPassword("password");

        String token = service.generateAccessToken(user);
//        Pattern p = Pattern.compile("[a-zA-Z0-9.-]*");
//        Matcher matcher = p.matcher(token);
        String name = service.getSubject(token);
        assertThat(token).isNotEmpty();
        assertThat(name).isEqualTo(user.getName());
//        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void givenExpiredDateToken_whenGetSubject_throwsExpiredJwtException() {
        assertThrows(ExpiredJwtException.class, () -> service.getSubject(token));
    }

    @Test
    public void givenInvalidToken_whenGetSubject_throwsMalformedJwtException() {
        String invalidToken = token.substring(1);
        assertThrows(MalformedJwtException.class, () -> service.getSubject(invalidToken));
    }
}
