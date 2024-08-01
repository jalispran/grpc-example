package com.pranjal.grpc.server.dao;

import com.pranjal.grpc.server.entity.User;
import com.pranjal.grpc.server.exception.AlreadyExistsException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserDAO {

    // Map of email, user object
    private final Map<String, User> users = new HashMap<>();

    public Collection<User> getAll() {
        return users.values();
    }

    public User create(String firstName, String lastName, String email) {
        Optional<User> userOptional = findByEmail(email);
        if (userOptional.isPresent()) {
            throw new AlreadyExistsException("User with name: " + firstName + " already exists");
        }
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
        save(user);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        if (users.containsKey(email)) {
            return Optional.of(users.get(email));
        }
        return Optional.empty();
    }

    public User save(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("email can not be null");
        }
        users.put(user.getEmail(), user);
        return user;
    }
}
