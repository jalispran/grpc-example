package com.pranjal.grpc.client.dto.input;

import com.pranjal.grpc.train.PurchaseTicketRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class PurchaseTicketInput {
    @NotBlank
    private String from;
    @NotBlank
    private String to;
    @NotNull
    private Double pricePaid;
    @NotNull
    private User user;

    public PurchaseTicketRequest toPurchaseTicketRequest() {
        if (!StringUtils.hasText(from)
                || !StringUtils.hasText(to)
                || pricePaid == null
                || user == null) {
            throw new IllegalArgumentException("mandatory fields missing");
        }

        return PurchaseTicketRequest.newBuilder()
                .setFrom(getFrom())
                .setTo(getTo())
                .setPricePaid(getPricePaid())
                .setUser(getUser().toGrpcUser())
                .build();
    }
}
