package com.pranjal.grpc.server.dao;

import com.pranjal.grpc.server.entity.User;
import com.pranjal.grpc.server.exception.AlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    public void testGetAll() {
        User user1 = new User("FirstName", "LastName", "first.last@example.com");
        User user2 = new User("FirstName1", "LastName", "first1.last@example.com");

        userDAO.save(user1);
        userDAO.save(user2);

        Collection<User> users = userDAO.getAll();
        assertEquals(2, users.size(), "There should be 2 users");
    }

    @Test
    public void testCreateUser() {
        User user = userDAO.create("FirstName", "LastName", "first.last@example.com");

        assertNotNull(user, "User should be created");
        assertEquals("FirstName", user.getFirstName(), "First name should match");
        assertEquals("LastName", user.getLastName(), "Last name should match");
        assertEquals("first.last@example.com", user.getEmail(), "Email should match");
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        userDAO.create("FirstName", "LastName", "first.last@example.com");

        AlreadyExistsException exception = assertThrows(
                AlreadyExistsException.class,
                () -> userDAO.create("FirstName1", "LastName", "first.last@example.com"),
                "Expected create() to throw AlreadyExistsException"
        );
        assertEquals("User with name: FirstName1 already exists", exception.getMessage());
    }

    @Test
    public void testFindByEmail() {
        User user = new User("FirstName", "LastName", "first.last@example.com");
        userDAO.save(user);

        Optional<User> foundUser = userDAO.findByEmail("first.last@example.com");
        assertTrue(foundUser.isPresent(), "User should be found by email");
        assertEquals("FirstName", foundUser.get().getFirstName(), "First name should match");
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<User> foundUser = userDAO.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent(), "User should not be found");
    }

    @Test
    public void testSaveUser() {
        User user = new User("FirstName", "LastName", "first.last@example.com");
        User savedUser = userDAO.save(user);

        assertEquals(user, savedUser, "Saved user should match the input user");
    }

    @Test
    public void testSaveUserWithNullEmail() {
        User user = new User("FirstName", "LastName", null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userDAO.save(user),
                "Expected save() to throw IllegalArgumentException"
        );
        assertEquals("email can not be null", exception.getMessage());
    }
}
