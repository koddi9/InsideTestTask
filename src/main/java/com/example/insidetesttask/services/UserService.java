package com.example.insidetesttask.services;

import com.example.insidetesttask.dao.UserRepository;
import com.example.insidetesttask.dto.JsonMessage;
import com.example.insidetesttask.exceptions.BadCredentialsException;
import com.example.insidetesttask.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository repository;
    @Autowired
    AuthService authService;

    private final String WRONG_NAME_EXC_MSG = "No such a user with name: ";
    private final String WRONG_PASS_EXC_MSG = "Wrong password was entered";

    public User findUser(String name, String password) {
        User user = findUserByName(name);

        if (!user.getPassword().equals(password)) {
            throw new BadCredentialsException(WRONG_PASS_EXC_MSG);
        }
        return user;
    }

    public User findUser(String token, JsonMessage jsonMsg) {
        String name = authService.getSubject(token);
        authService.validateNames(jsonMsg.getName(), name);

        return findUserByName(name);
    }

    private User findUserByName(String name) {
        return repository.findUserByName(name)
                .orElseThrow(() -> new BadCredentialsException(WRONG_NAME_EXC_MSG + name));
    }
}
