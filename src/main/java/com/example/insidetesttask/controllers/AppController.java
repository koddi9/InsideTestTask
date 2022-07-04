package com.example.insidetesttask.controllers;

import com.example.insidetesttask.dto.JsonMessage;
import com.example.insidetesttask.models.User;
import com.example.insidetesttask.services.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class AppController {
    @Autowired
    AppService service;

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> getUserToken(@RequestBody User user) {
        String token = service.getToken(user);
        return Collections.singletonMap("token",token);
    }

    @PostMapping("/msg")
    @ResponseStatus(HttpStatus.OK)
    public void postMessageToDb(@RequestBody JsonMessage jsonMessage, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationValue) {
        service.saveMessage(jsonMessage, authorizationValue);
    }

    @PostMapping("/msgs")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getUserMessagesHistory(@RequestBody JsonMessage jsonMessage, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationValue) {
        return service.getMessages(jsonMessage,authorizationValue);
    }
}
