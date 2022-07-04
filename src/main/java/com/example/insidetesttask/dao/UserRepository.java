package com.example.insidetesttask.dao;

import com.example.insidetesttask.models.Message;
import com.example.insidetesttask.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByName(String name);
}
