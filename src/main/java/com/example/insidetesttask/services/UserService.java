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

    /**
     * Finds the user from database (DB) by giving name and check the equality of the passwords
     * @param name Searched username
     * @param password User's password
     * @throws BadCredentialsException If the user with provided name doesn't exist or the password is invalid
     * @return User from DB
     */
    public User findUser(String name, String password) {
        User user = findUserByName(name);

        if (!user.getPassword().equals(password)) {
            throw new BadCredentialsException(WRONG_PASS_EXC_MSG);
        }
        return user;
    }

    /**
     * Extracts name from the provided token and checks with the username from client message.
     * If the extraction and validation successfully done, will get the user from database (DB).
     * Otherwise throw an exception.
     * @param token Token value
     * @param jsonMsg Client message
     * @throws io.jsonwebtoken.JwtException during parsing. For example, if token value is not valid or an expiration time is ended
     * @throws com.example.insidetesttask.exceptions.NotCompatibleNameWithTokenSubject If the names are not equal
     * @return User from DB
     */
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
