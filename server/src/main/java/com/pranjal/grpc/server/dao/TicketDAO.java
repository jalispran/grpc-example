package com.pranjal.grpc.server.dao;

import com.pranjal.grpc.server.entity.Ticket;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TicketDAO {
    private final Map<UUID, Ticket> tickets = new HashMap<>();

    public void deleteByTicketId(UUID ticketId) {
        tickets.remove(ticketId);
    }

    public Optional<Ticket> getTicketBySeatId(UUID seatId) {
        if (seatId == null) {
            throw new IllegalArgumentException("seatId can not be null");
        }
        return tickets.values().stream()
                .filter(ticket -> ticket.getSeatId().equals(seatId))
                .findFirst();
    }

    public Optional<Ticket> getTicketByUser(String userEmail) {
        if (!StringUtils.hasText(userEmail)) {
            throw new IllegalArgumentException("email can not be null when finding ticket");
        }
        return tickets.values().stream()
                .filter(ticket -> userEmail.equals(ticket.getUserEmail()))
                .findFirst();
    }

    public Collection<Ticket> getAll() {
        return tickets.values();
    }

    public Ticket save(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("ticket can not be null");
        }
        if (!StringUtils.hasText(ticket.getFromLocation())
                || !StringUtils.hasText(ticket.getToLocation())
                || ticket.getPricePaid() == null
                || ticket.getSeatId() == null
                || ticket.getUserEmail() == null) {
            throw new IllegalArgumentException("ghost tickets are not allowed");
        }
        if (ticket.getTicketId() == null) {
            ticket.setTicketId(UUID.randomUUID());
        }
        if (tickets.containsKey(ticket.getTicketId())) {
            throw new IllegalArgumentException("ticket already exists");
        }
        ticket.setPurchaseDate(ZonedDateTime.now());
        tickets.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    public List<Ticket> saveAll(List<Ticket> ticketList) {
        return ticketList.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }
}
