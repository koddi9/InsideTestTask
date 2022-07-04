package com.example.insidetesttask.controllers;

import com.example.insidetesttask.dto.JsonMessage;
import com.example.insidetesttask.exceptions.BadCredentialsException;
import com.example.insidetesttask.exceptions.InvalidPropertyStructureException;
import com.example.insidetesttask.exceptions.NotCompatibleNameWithTokenSubject;
import com.example.insidetesttask.models.Message;
import com.example.insidetesttask.models.User;
import com.example.insidetesttask.services.AppService;
import com.example.insidetesttask.services.AuthService;
import com.example.insidetesttask.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AppControllerTests {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AppService appService;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserService userService;

    String jsonUser;
    String jsonMessage;

    @BeforeEach
    public void beforeEach() throws Exception {
        ObjectMapper om = new ObjectMapper();

        User user = new User();
        user.setName("name");
        user.setPassword("password");
        this.jsonUser = om.writeValueAsString(user);

        JsonMessage jsonMsg = new JsonMessage();
        jsonMsg.setName("name");
        jsonMsg.setMessage("message");
        this.jsonMessage = om.writeValueAsString(jsonMsg);
    }

    @Test
    public void givenValidJsonUser_whenGetToken_thenReturnOk() throws Exception {
        String token = "token";
        when(appService.getToken(any())).thenReturn(token);

        mvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenJsonUser_whenServiceThrowsRuntimeException_returnsInternalServerError() throws Exception {
        doThrow(RuntimeException.class).when(appService).getToken(any());

        mvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void givenInvalidJsonUser_whenServiceThrowsBadCredentialsException_returnsBadRequest() throws Exception {
        doThrow(BadCredentialsException.class).when(appService).getToken(any());

        mvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidJsonMsg_AndAuthValue_whenSaveMessage_thenReturnOk() throws Exception {
        String authValue = "authorization";
        when(appService.saveMessage(any(), anyString())).thenReturn(new Message());

        mvc.perform(post("/msg")
                        .header(HttpHeaders.AUTHORIZATION, authValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMessage))
                .andExpect(status().isOk());
    }

    @Test
    public void givenJsonMsg_AndAuthValue_whenServiceThrowsInvalidPropertyStructureException_returnsBadRequest() throws Exception {
        String authValue = "authorization";
        doThrow(InvalidPropertyStructureException.class).when(appService).saveMessage(any(), anyString());

        mvc.perform(post("/msg")
                        .header(HttpHeaders.AUTHORIZATION, authValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMessage))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenJsonMsg_AndAuthValue_whenServiceThrowsNotCompatibleNameWithTokenSubject_returnsBadRequest() throws Exception {
        String authValue = "authorization";
        doThrow(NotCompatibleNameWithTokenSubject.class).when(appService).saveMessage(any(), anyString());

        mvc.perform(post("/msg")
                        .header(HttpHeaders.AUTHORIZATION, authValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMessage))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenValidJsonMsg_AndAuthValue_whenGetMessages_thenReturnOk() throws Exception {
        String authValue = "authorization";

        when(appService.getMessages(any(), anyString())).thenReturn(Collections.emptyList());

        mvc.perform(post("/msgs")
                        .header(HttpHeaders.AUTHORIZATION, authValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMessage))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
