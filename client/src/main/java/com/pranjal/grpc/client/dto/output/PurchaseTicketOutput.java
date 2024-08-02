package com.pranjal.grpc.client.dto.output;

import com.pranjal.grpc.client.dto.input.User;
import com.pranjal.grpc.train.PurchaseTicketResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseTicketOutput {
    private String from;
    private String to;
    private Double pricePaid;
    private String seatSection;
    private String seatNumber;
    private User user;

    public static PurchaseTicketOutput fromGrpcResponse(PurchaseTicketResponse response) {
        return builder()
                .from(response.getFrom())
                .to(response.getTo())
                .pricePaid(response.getPricePaid())
                .seatSection(response.getSeatSection())
                .seatNumber(response.getSeatNumber())
                .user(User.fromGrpcUser(response.getUser()))
                .build();
    }
}
