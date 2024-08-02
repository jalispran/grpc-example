package com.pranjal.grpc.server.service;

import com.pranjal.grpc.server.dao.SeatDAO;
import com.pranjal.grpc.server.dao.TicketDAO;
import com.pranjal.grpc.server.dao.UserDAO;
import com.pranjal.grpc.server.entity.Seat;
import com.pranjal.grpc.server.entity.Ticket;
import com.pranjal.grpc.server.entity.User;
import com.pranjal.grpc.train.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TrainTicketServiceTest {
    private final TicketDAO ticketDAO = mock(TicketDAO.class);
    private final UserDAO userDAO = mock(UserDAO.class);
    private final SeatDAO seatDAO = mock(SeatDAO.class);
    @InjectMocks
    private TrainTicketService trainTicketService;

    private User user;
    private Seat seat;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        seat = new Seat();
        seat.setSeatId(UUID.randomUUID());
        seat.setSeatSection("A");
        seat.setSeatNumber("1");
        seat.setUserEmail(user.getEmail());

        ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID());
        ticket.setFromLocation("London");
        ticket.setToLocation("Paris");
        ticket.setPricePaid(20.0);
        ticket.setUserEmail(user.getEmail());
        ticket.setSeatId(seat.getSeatId());
    }

    @Test
    void testGetReceiptSuccess() {
        when(userDAO.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(ticketDAO.getTicketByUser(anyString())).thenReturn(Optional.of(ticket));
        when(seatDAO.getById(any(UUID.class))).thenReturn(Optional.of(seat));

        GetReceiptRequest request = GetReceiptRequest.newBuilder()
                .setEmail(user.getEmail())
                .build();
        StreamObserver<PurchaseTicketResponse> responseObserver = mock(StreamObserver.class);

        trainTicketService.getReceipt(request, responseObserver);

        verify(responseObserver).onNext(any(PurchaseTicketResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void testPurchaseTicketSuccess() {
        when(userDAO.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userDAO.create(anyString(), anyString(), anyString())).thenReturn(user);
        when(seatDAO.getAvailableSeat()).thenReturn(Optional.of(seat));
        when(seatDAO.save(any(Seat.class))).thenReturn(seat);
        when(ticketDAO.save(any(Ticket.class))).thenReturn(ticket);

        PurchaseTicketRequest request = PurchaseTicketRequest.newBuilder()
                .setFrom("London")
                .setTo("Paris")
                .setPricePaid(20.0)
                .setUser(com.pranjal.grpc.train.User.newBuilder()
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setEmail(user.getEmail())
                        .build())
                .build();
        StreamObserver<PurchaseTicketResponse> responseObserver = mock(StreamObserver.class);

        trainTicketService.purchaseTicket(request, responseObserver);

        verify(responseObserver).onNext(any(PurchaseTicketResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void testGetUsersBySectionSuccess() {
        when(seatDAO.getSections()).thenReturn(List.of("A", "B"));
        when(seatDAO.getAll()).thenReturn(List.of(seat));
        when(userDAO.findByEmail(anyString())).thenReturn(Optional.of(user));

        GetUsersBySectionRequest request = GetUsersBySectionRequest.newBuilder()
                .setSection("A")
                .build();
        StreamObserver<UserSeat> responseObserver = mock(StreamObserver.class);

        trainTicketService.getUsersBySection(request, responseObserver);

        verify(responseObserver).onNext(any(UserSeat.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void testRemoveUserSuccess() {
        when(seatDAO.deleteByEmail(anyString())).thenReturn(seat);
        when(ticketDAO.getTicketBySeatId(any(UUID.class))).thenReturn(Optional.of(ticket));

        RemoveUserRequest request = RemoveUserRequest.newBuilder()
                .setEmail(user.getEmail())
                .build();
        StreamObserver<RemoveUserResponse> responseObserver = mock(StreamObserver.class);

        trainTicketService.removeUser(request, responseObserver);

        verify(responseObserver).onNext(any(RemoveUserResponse.class));
        verify(responseObserver).onCompleted();
    }
}
