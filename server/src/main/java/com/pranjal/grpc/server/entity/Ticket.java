package com.pranjal.grpc.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private UUID ticketId;
    private String userEmail;
    private UUID seatId;
    private String fromLocation;
    private String toLocation;
    private Double pricePaid;
    private ZonedDateTime purchaseDate;
}
