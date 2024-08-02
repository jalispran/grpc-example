package com.pranjal.grpc.server.service;

import com.pranjal.grpc.server.dao.SeatDAO;
import com.pranjal.grpc.server.dao.TicketDAO;
import com.pranjal.grpc.server.dao.UserDAO;
import com.pranjal.grpc.server.entity.Seat;
import com.pranjal.grpc.server.entity.Ticket;
import com.pranjal.grpc.server.entity.User;
import com.pranjal.grpc.train.GetReceiptRequest;
import com.pranjal.grpc.train.GetUsersBySectionRequest;
import com.pranjal.grpc.train.ModifyUserSeatRequest;
import com.pranjal.grpc.train.PurchaseTicketRequest;
import com.pranjal.grpc.train.PurchaseTicketResponse;
import com.pranjal.grpc.train.RemoveUserRequest;
import com.pranjal.grpc.train.RemoveUserResponse;
import com.pranjal.grpc.train.TrainTicketServiceGrpc.TrainTicketServiceImplBase;
import com.pranjal.grpc.train.UserSeat;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class TrainTicketService extends TrainTicketServiceImplBase {
    private final TicketDAO ticketDAO;
    private final UserDAO userDAO;
    private final SeatDAO seatDAO;

    @Override
    public void getReceipt(GetReceiptRequest request, StreamObserver<PurchaseTicketResponse> responseObserver) {
        String email = request.getEmail();
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("email can not be empty");
        }

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        Ticket ticket = ticketDAO.getTicketByUser(email)
                .orElseThrow(() -> new IllegalArgumentException("no tickets for this user"));

        UUID seatId = ticket.getSeatId();
        Seat seat = seatDAO.getById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("seat not found: " + seatId));

        responseObserver.onNext(PurchaseTicketResponse.newBuilder()
                .setFrom(ticket.getFromLocation())
                .setTo(ticket.getToLocation())
                .setPricePaid(ticket.getPricePaid())
                .setSeatSection(seat.getSeatSection())
                .setSeatNumber(seat.getSeatNumber())
                .setUser(com.pranjal.grpc.train.User.newBuilder()
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setEmail(user.getEmail())
                        .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void purchaseTicket(PurchaseTicketRequest request, StreamObserver<PurchaseTicketResponse> responseObserver) {
        String from = request.getFrom();
        String to = request.getTo();
        Double pricePaid = request.getPricePaid();
        String firstName = request.getUser().getFirstName();
        String lastName = request.getUser().getLastName();
        String email = request.getUser().getEmail();

        // Create user if not found
        User user = userDAO.findByEmail(email)
                .orElseGet(() -> userDAO.create(firstName, lastName, email));

        Seat availableSeat = seatDAO.getAvailableSeat()
                .orElseThrow(() -> new IllegalArgumentException("No seats are available"));
        availableSeat.setUserEmail(user.getEmail());
        Seat savedSeat = seatDAO.save(availableSeat);
        Ticket ticket = Ticket.builder()
                .fromLocation(from)
                .toLocation(to)
                .pricePaid(pricePaid)
                .userEmail(email)
                .seatId(savedSeat.getSeatId())
                .build();
        Ticket savedTicket = ticketDAO.save(ticket);
        responseObserver.onNext(PurchaseTicketResponse.newBuilder()
                .setFrom(savedTicket.getFromLocation())
                .setTo(savedTicket.getToLocation())
                .setPricePaid(savedTicket.getPricePaid())
                .setSeatSection(savedSeat.getSeatSection())
                .setSeatNumber(savedSeat.getSeatNumber())
                .setUser(com.pranjal.grpc.train.User.newBuilder()
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setEmail(user.getEmail())
                        .build())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUsersBySection(GetUsersBySectionRequest request, StreamObserver<UserSeat> responseObserver) {
        if (!seatDAO.getSections().contains(request.getSection())) {
            throw new IllegalArgumentException("invalid section");
        }
        seatDAO.getAll().stream()
                .filter(seat -> seat.getSeatSection().equals(request.getSection()))
                .forEach(seat -> {
                    Optional<User> userOptional = userDAO.findByEmail(seat.getUserEmail());
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        responseObserver.onNext(UserSeat.newBuilder()
                                .setSeatSection(seat.getSeatSection())
                                .setSeatNumber(seat.getSeatNumber())
                                .setUser(com.pranjal.grpc.train.User.newBuilder()
                                        .setEmail(user.getEmail())
                                        .setFirstName(user.getFirstName())
                                        .setLastName(user.getLastName())
                                        .build())
                                .build());
                    }
                });
        responseObserver.onCompleted();
    }

    @Override
    public void removeUser(RemoveUserRequest request, StreamObserver<RemoveUserResponse> responseObserver) {
        String email = request.getEmail();
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("email can not be null/empty");
        }
        Seat deletedSeat = seatDAO.deleteByEmail(email);
        ticketDAO.getTicketBySeatId(deletedSeat.getSeatId())
                .ifPresent(ticket -> ticketDAO.deleteByTicketId(ticket.getTicketId()));
        responseObserver.onNext(RemoveUserResponse.newBuilder()
                .setMessage("removed")
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void modifyUserSeat(ModifyUserSeatRequest request, StreamObserver<PurchaseTicketResponse> responseObserver) {
        String email = request.getEmail();
        String newSeatSection = request.getNewSeatSection();
        String newSeatNumber = request.getNewSeatNumber();
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("email can not be null");
        }
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        if (!seatDAO.getSections().contains(newSeatSection)) {
            throw new IllegalArgumentException("Invalid seat section selected");
        }
        Seat deletedSeat = seatDAO.deleteByEmail(email);
        Optional<Ticket> ticketBySeatId = ticketDAO.getTicketBySeatId(deletedSeat.getSeatId());
        if (ticketBySeatId.isEmpty()) {
            throw new IllegalArgumentException("no ticket found");
        }
        Ticket deletedTicket = ticketBySeatId.get();
        ticketDAO.deleteByTicketId(deletedTicket.getTicketId());
        Seat newSeat = Seat.builder()
                .userEmail(email)
                .seatSection(newSeatSection)
                .seatNumber(newSeatNumber)
                .build();
        Seat savedSeat = seatDAO.save(newSeat);
        Ticket ticket = Ticket.builder()
                .fromLocation(deletedTicket.getFromLocation())
                .toLocation(deletedTicket.getToLocation())
                .pricePaid(deletedTicket.getPricePaid())
                .userEmail(email)
                .seatId(savedSeat.getSeatId())
                .build();
        Ticket savedTicket = ticketDAO.save(ticket);
        responseObserver.onNext(PurchaseTicketResponse.newBuilder()
                .setFrom(savedTicket.getFromLocation())
                .setTo(savedTicket.getToLocation())
                .setPricePaid(savedTicket.getPricePaid())
                .setSeatSection(savedSeat.getSeatSection())
                .setSeatNumber(savedSeat.getSeatNumber())
                .setUser(com.pranjal.grpc.train.User.newBuilder()
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setEmail(user.getEmail())
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
