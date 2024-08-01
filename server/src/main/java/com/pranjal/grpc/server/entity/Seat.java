package com.pranjal.grpc.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private UUID seatId;
    private String userEmail;
    private String seatSection;
    private String seatNumber;
}
