package com.example.insidetesttask.services;

import com.example.insidetesttask.dto.JsonMessage;
import com.example.insidetesttask.exceptions.InvalidPropertyStructureException;
import com.example.insidetesttask.models.Message;
import com.example.insidetesttask.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
public class AppServiceTests {
    @Autowired
    AppService service;
    @MockBean
    AuthService authService;
    @MockBean
    UserService userService;

    User user;
    JsonMessage jsonMessage;
    String prefix = "Bearer_";
    @Value("${app.test.token}")
    String token;

    @BeforeEach
    public void beforeEach() {
        User user = new User();
        user.setName("name");
        user.setPassword("password");

        List<Message> messages = new ArrayList<Message>();
        for (int i = 1; i < 21; i++) {
            messages.add(
                    new Message("Message" + i, user)
            );
        }
        user.setMessages(messages);
        this.user = user;

        JsonMessage jsonMessage = new JsonMessage();
        jsonMessage.setMessage("message");
        this.jsonMessage = jsonMessage;
    }

    @Test
    public void givenValidJsonMessage_AndAuthValue_whenSaveMessage_returnsMessage() {

        when(userService.findUser(token, jsonMessage)).thenReturn(user);
        Message message = service.saveMessage(jsonMessage, prefix + token);
        assertThat(message.getValue()).isEqualTo("message");
        assertThat(message.getUser()).isEqualTo(user);
    }

    @Test
    public void givenValidJsonMessage_AndInvalidAuthValue_whenSaveMessage_throwsInvalidHeaderStructureException() {

        Exception ex = assertThrows(InvalidPropertyStructureException.InvalidHeaderStructureException.class,
                () -> service.saveMessage(jsonMessage, "token"));
        assertThat(ex.getMessage()).contains("Invalid header");
    }

    @Test
    public void givenValidJsonMessage_AndAuthValue_whenGetMessages_returnsMessagesList() {
        int messasgesCount = 5;
        List<String> checkedMsgs = new ArrayList<>();
        for (int i = user.getMessages().size(); i > user.getMessages().size()-messasgesCount; i--) {
            checkedMsgs.add("Message" + i);
        }
        jsonMessage.setMessage("history " + messasgesCount);

        when(userService.findUser(token, jsonMessage)).thenReturn(user);
        List<String> messages = service.getMessages(jsonMessage, prefix + token);
        assertThat(messages.size()).isLessThanOrEqualTo(messasgesCount);
        messages.stream().forEach(msg -> assertThat(msg).isIn(checkedMsgs));
    }

    @Test
    public void givenInvalidJsonMessage_AndValidAuthValue_whenGetMessages_throwsInvalidMessageStructureException() {
        when(userService.findUser(token, jsonMessage)).thenReturn(user);
        Exception ex = assertThrows(InvalidPropertyStructureException.InvalidMessageStructureException.class,
                () -> service.getMessages(jsonMessage, prefix + token));
        assertThat(ex.getMessage()).contains("Invalid message");
    }
}
