package com.example.insidetesttask.services;

import com.example.insidetesttask.dao.UserRepository;
import com.example.insidetesttask.dto.JsonMessage;
import com.example.insidetesttask.exceptions.BadCredentialsException;
import com.example.insidetesttask.models.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserService service;
    @MockBean
    UserRepository repository;
    @MockBean
    AuthService authService;

    User jsonUser;

    @BeforeEach
    public void beforeEach() {
        User user = new User();
        user.setName("name");
        user.setPassword("password");
        this.jsonUser = user;
    }

    @Test
    public void givenValidNameAndPassword_whenFindUser_returnsUser() {
        when(repository.findUserByName(anyString())).thenReturn(Optional.of(jsonUser));
        User dbUser = service.findUser(jsonUser.getName(), jsonUser.getPassword());
        assertThat(dbUser.getName()).isEqualTo(jsonUser.getName());
        assertThat(dbUser.getPassword()).isEqualTo(jsonUser.getPassword());
    }

    @Test
    public void givenInvalidNameAndPassword_whenFindUser_throwsBadCredentialException() {
        when(repository.findUserByName(anyString())).thenReturn(Optional.empty());
        Exception ex = assertThrows(BadCredentialsException.class, () -> service.findUser(jsonUser.getName(), jsonUser.getPassword()));
        assertThat(ex.getMessage()).contains("No such a user with name");
    }

    @Test
    public void givenNameAndInvalidPassword_whenFindUser_throwsBadCredentialException() {
        User dbUser = new User();
        dbUser.setName("name");
        dbUser.setPassword("realPassword");

        when(repository.findUserByName(anyString())).thenReturn(Optional.of(dbUser));
        Exception ex = assertThrows(BadCredentialsException.class, () -> service.findUser(jsonUser.getName(), jsonUser.getPassword()));
        assertThat(ex.getMessage()).contains("Wrong password");
    }


    @Test
    public void givenTokenAndJsonMsg_whenFindUser_returnsUser() {
        String token = "token";
        String name = jsonUser.getName();
        JsonMessage jsonMessage = new JsonMessage();

        when(authService.getSubject(anyString())).thenReturn(name);
        doNothing().when(authService).validateNames(anyString(), anyString());
        when(repository.findUserByName(anyString())).thenReturn(Optional.of(jsonUser));

        User dbUser = service.findUser(token, jsonMessage);
        assertThat(dbUser.getName()).isEqualTo(jsonUser.getName());
        assertThat(dbUser.getPassword()).isEqualTo(jsonUser.getPassword());
    }

    @Test
    public void givenTokenAndJsonMsg_whenGetSubject_throwsJwtException() {
        String token = "token";
        JsonMessage jsonMessage = new JsonMessage();
        doThrow(JwtException.class).when(authService).getSubject(anyString());

        assertThrows(JwtException.class, () -> service.findUser(token, jsonMessage));
        verify(repository, times(0)).findUserByName(anyString());
        verify(authService, times(0)).validateNames(anyString(),anyString());
    }

//    @Test
//    public void givenTokenAndJsonMsg_whenGetSubject_throwsNotCompatibleNameWithTokenSubjectException() {
//        String token = "token";
//        String name = jsonUser.getName();
//        JsonMessage jsonMessage = new JsonMessage();
//
//        when(authService.getSubject(any(), anyString())).thenReturn(name);
//        doThrow(NotCompatibleNameWithTokenSubject.class).when(authService).validateNames(anyString(), anyString());
//
//        assertThrows(NotCompatibleNameWithTokenSubject.class, () -> service.findUser(token, jsonMessage));
//        verify(repository, times(0)).findUserByName(anyString());
//    }
}
