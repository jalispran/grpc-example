package com.pranjal.grpc.client.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;

    public com.pranjal.grpc.train.User toGrpcUser() {
        return com.pranjal.grpc.train.User.newBuilder()
                .setFirstName(this.firstName)
                .setLastName(this.lastName)
                .setEmail(this.email)
                .build();
    }

    public static User fromGrpcUser(com.pranjal.grpc.train.User grpcUser) {
        return builder()
                .firstName(grpcUser.getFirstName())
                .lastName(grpcUser.getLastName())
                .email(grpcUser.getEmail())
                .build();
    }
}
