package com.example.insidetesttask.services;

import com.example.insidetesttask.dto.JsonMessage;
import com.example.insidetesttask.models.Message;
import com.example.insidetesttask.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.insidetesttask.exceptions.InvalidPropertyStructureException.InvalidHeaderStructureException;
import static com.example.insidetesttask.exceptions.InvalidPropertyStructureException.InvalidMessageStructureException;

@Service
public class AppService {
    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;

    public String getToken(User jsonUser) {
        String name = jsonUser.getName();
        String password = jsonUser.getPassword();
        User user = userService.findUser(name, password);
        return authService.generateAccessToken(user);
    }

    @Transactional
    public Message saveMessage(JsonMessage jsonMessage, String authValue) {
        String token = parseToken(authValue);
        User user = userService.findUser(token, jsonMessage);
        Message msg = new Message(jsonMessage.getMessage(), user);
        user.getMessages().add(msg);
        return msg;
    }

    public List<String> getMessages(JsonMessage jsonMessage, String authValue) {
        String token = parseToken(authValue);
        User user = userService.findUser(token, jsonMessage);
        int count = parseMsgCount(jsonMessage);
        Collections.reverse(user.getMessages());
        return user.getMessages().stream().map(Message::getValue).limit(count).collect(Collectors.toList());
    }

    /**
     *Gets the token from the Authorization header value getting by the Http request.
     *Pattern of the value: Bearer_token (e.g Bearer_eyJhbGc...)
     * @param authValue - The raw value of the Authorization header
     * @throws InvalidHeaderStructureException If the structure of the provided param does not match the pattern
     * @return Token value as a string
     *
     */
    private String parseToken(String authValue) {
        if (!authValue.startsWith("Bearer_")) {
            throw new InvalidHeaderStructureException(HttpHeaders.AUTHORIZATION);
        }
        return authValue.substring(7).trim();
    }

    /**
     * Parses client message, validates structure and extracts the count of the response messages
     * Pattern of the message: history count (e.g history 5)
     * @param jsonMessage Client message
     * @throws InvalidMessageStructureException If the structure doesn't contain key word "history" or satisfied the pattern
     * @return Response messages count
     */
    private int parseMsgCount(JsonMessage jsonMessage) {
        try {
            String[] msgParts = new String[2];
            msgParts = jsonMessage.getMessage().split("\\s{1}");
            String keyWord = msgParts[0];
            String countersString = msgParts[1];
            if (!keyWord.equals("history")) {
                throw new RuntimeException("Wrong key word: " + keyWord);
            }
            return Integer.parseInt(countersString);
        } catch (RuntimeException e) {
            throw new InvalidMessageStructureException(e.getMessage());
        }
    }
}
