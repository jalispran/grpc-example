package com.pranjal.grpc.server.service;

import com.google.protobuf.Empty;
import com.pranjal.grpc.server.dao.UserDAO;
import com.pranjal.grpc.server.entity.User;
import com.pranjal.grpc.user.CreateUserRequest;
import com.pranjal.grpc.user.CreateUserResponse;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final UserDAO userDAO = mock(UserDAO.class);

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Test
    void createUser_success() {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .build();

        when(userDAO.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userDAO.save(any(User.class))).thenReturn(user);

        StreamRecorder<CreateUserResponse> responseObserver = StreamRecorder.create();

        userService.createUser(request, responseObserver);

        assertTrue(responseObserver.getValues().size() > 0);
        assertTrue(responseObserver.getValues().get(0).getEmail().equals(user.getEmail()));
        verify(userDAO, times(1)).save(any(User.class));
    }

    @Test
    void createUser_existingUser_throwsException() {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .build();

        when(userDAO.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        StreamRecorder<CreateUserResponse> responseObserver = StreamRecorder.create();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request, responseObserver);
        });

        String expectedMessage = "user already exists";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userDAO, times(1)).findByEmail(user.getEmail());
        verify(userDAO, times(0)).save(any(User.class));
    }

    @Test
    void createUser_invalidInput_throwsException() {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setEmail("")
                .setFirstName("")
                .setLastName("")
                .build();

        StreamRecorder<CreateUserResponse> responseObserver = StreamRecorder.create();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request, responseObserver);
        });

        String expectedMessage = "first name, last names and email can not be empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userDAO, times(0)).findByEmail(anyString());
        verify(userDAO, times(0)).save(any(User.class));
    }

    @Test
    void getAllUsers_success() {
        User user2 = User.builder()
                .email("test2@example.com")
                .firstName("Test2")
                .lastName("User2")
                .build();

        when(userDAO.getAll()).thenReturn(Arrays.asList(user, user2));

        StreamRecorder<CreateUserResponse> responseObserver = StreamRecorder.create();

        userService.getAllUsers(Empty.getDefaultInstance(), responseObserver);

        assertTrue(responseObserver.getValues().size() == 2);
        verify(userDAO, times(1)).getAll();
    }
}
