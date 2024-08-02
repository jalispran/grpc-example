package com.pranjal.grpc.client.dto.output;

import com.pranjal.grpc.client.dto.input.User;
import com.pranjal.grpc.train.UserSeat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSeatOutput {
    private String seatSection;
    private String seatNumber;
    private User user;

    public static UserSeatOutput fromGrpcUserSeat(UserSeat userSeat) {
        return builder()
                .seatSection(userSeat.getSeatSection())
                .seatNumber(userSeat.getSeatNumber())
                .user(User.fromGrpcUser(userSeat.getUser()))
                .build();
    }
}
