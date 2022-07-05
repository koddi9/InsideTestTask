package com.example.insidetesttask.dao;

import com.example.insidetesttask.models.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @Test
    public void whenFindUserByName_thenReturnUser() {

        User user = new User();
        user.setName("name");
        user.setPassword("password");

        entityManager.persist(user);
        entityManager.flush();

        User found = repository.findUserByName(user.getName()).get();

        assertThat(found.getName())
                .isEqualTo(user.getName());
    }
}
