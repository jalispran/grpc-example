package com.pranjal.grpc.server.service;

import com.google.protobuf.Empty;
import com.pranjal.grpc.server.dao.UserDAO;
import com.pranjal.grpc.server.entity.User;
import com.pranjal.grpc.user.CreateUserRequest;
import com.pranjal.grpc.user.CreateUserResponse;
import com.pranjal.grpc.user.UserServiceGrpc.UserServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserService extends UserServiceImplBase {
    private final UserDAO userDAO;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        if (!StringUtils.hasText(request.getFirstName())
                || !StringUtils.hasText(request.getLastName())
                || !StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("first name, last names and email can not be empty");
        }
        Optional<User> userOptional = userDAO.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalArgumentException("user already exists");
        }

        User savedUser = userDAO.save(User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build());

        responseObserver.onNext(CreateUserResponse.newBuilder()
                .setEmail(savedUser.getEmail())
                .setFirstName(savedUser.getFirstName())
                .setLastName(savedUser.getLastName())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAllUsers(Empty request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("All users requested");
        try {
            for (User user : userDAO.getAll()) {
                log.info("User sent");
                responseObserver.onNext(CreateUserResponse.newBuilder()
                        .setEmail(user.getEmail())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .build());
            }
        } catch (Exception e) {
            responseObserver.onError(e);
        }
        responseObserver.onCompleted();
        log.info("All users sent");
    }

}
