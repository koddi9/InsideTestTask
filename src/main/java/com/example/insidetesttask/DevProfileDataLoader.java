package com.example.insidetesttask;

import com.example.insidetesttask.dao.UserRepository;
import com.example.insidetesttask.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevProfileDataLoader implements ApplicationRunner {

    @Autowired
    private UserRepository repository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User defaultUser = new User();
        defaultUser.setName("user");
        defaultUser.setPassword("password");
        repository.save(defaultUser);
    }
}
