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

    private String parseToken(String authValue) {
        if (!authValue.startsWith("Bearer_")) {
            throw new InvalidHeaderStructureException(HttpHeaders.AUTHORIZATION);
        }
        return authValue.substring(7).trim();
    }

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
