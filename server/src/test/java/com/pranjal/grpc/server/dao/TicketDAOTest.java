package com.pranjal.grpc.server.dao;

import com.pranjal.grpc.server.entity.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketDAOTest {

    private TicketDAO ticketDAO;

    @BeforeEach
    public void setUp() {
        ticketDAO = new TicketDAO();
    }

    @Test
    public void testDeleteByTicketId() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID());
        ticket.setFromLocation("A");
        ticket.setToLocation("B");
        ticket.setPricePaid(100.0);
        ticket.setSeatId(UUID.randomUUID());
        ticket.setUserEmail("user@example.com");
        ticketDAO.save(ticket);

        UUID ticketId = ticket.getTicketId();
        ticketDAO.deleteByTicketId(ticketId);

        Optional<Ticket> result = ticketDAO.getTicketBySeatId(ticket.getSeatId());
        assertTrue(result.isEmpty(), "Ticket should be deleted");
    }

    @Test
    public void testGetTicketBySeatId() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID());
        ticket.setFromLocation("A");
        ticket.setToLocation("B");
        ticket.setPricePaid(100.0);
        ticket.setSeatId(UUID.randomUUID());
        ticket.setUserEmail("user@example.com");
        ticketDAO.save(ticket);

        Optional<Ticket> result = ticketDAO.getTicketBySeatId(ticket.getSeatId());
        assertTrue(result.isPresent(), "Ticket should be found by seat ID");
        assertEquals(ticket.getTicketId(), result.get().getTicketId(), "Ticket ID should match");
    }

    @Test
    public void testGetTicketByUser() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID());
        ticket.setFromLocation("A");
        ticket.setToLocation("B");
        ticket.setPricePaid(100.0);
        ticket.setSeatId(UUID.randomUUID());
        ticket.setUserEmail("user@example.com");
        ticketDAO.save(ticket);

        Optional<Ticket> result = ticketDAO.getTicketByUser("user@example.com");
        assertTrue(result.isPresent(), "Ticket should be found by user email");
        assertEquals(ticket.getTicketId(), result.get().getTicketId(), "Ticket ID should match");
    }

    @Test
    public void testGetAll() {
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId(UUID.randomUUID());
        ticket1.setFromLocation("A");
        ticket1.setToLocation("B");
        ticket1.setPricePaid(100.0);
        ticket1.setSeatId(UUID.randomUUID());
        ticket1.setUserEmail("user1@example.com");

        Ticket ticket2 = new Ticket();
        ticket2.setTicketId(UUID.randomUUID());
        ticket2.setFromLocation("C");
        ticket2.setToLocation("D");
        ticket2.setPricePaid(200.0);
        ticket2.setSeatId(UUID.randomUUID());
        ticket2.setUserEmail("user2@example.com");

        ticketDAO.save(ticket1);
        ticketDAO.save(ticket2);

        Collection<Ticket> tickets = ticketDAO.getAll();
        assertEquals(2, tickets.size(), "There should be 2 tickets");
    }

    @Test
    public void testSave() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID());
        ticket.setFromLocation("A");
        ticket.setToLocation("B");
        ticket.setPricePaid(100.0);
        ticket.setSeatId(UUID.randomUUID());
        ticket.setUserEmail("user@example.com");

        Ticket savedTicket = ticketDAO.save(ticket);
        assertNotNull(savedTicket.getTicketId(), "Ticket ID should be generated");
        assertEquals(ticket.getSeatId(), savedTicket.getSeatId(), "Seat ID should match");
    }

    @Test
    public void testSaveAll() {
        Ticket ticket1 = new Ticket();
        ticket1.setTicketId(UUID.randomUUID());
        ticket1.setFromLocation("A");
        ticket1.setToLocation("B");
        ticket1.setPricePaid(100.0);
        ticket1.setSeatId(UUID.randomUUID());
        ticket1.setUserEmail("user1@example.com");

        Ticket ticket2 = new Ticket();
        ticket2.setTicketId(UUID.randomUUID());
        ticket2.setFromLocation("C");
        ticket2.setToLocation("D");
        ticket2.setPricePaid(200.0);
        ticket2.setSeatId(UUID.randomUUID());
        ticket2.setUserEmail("user2@example.com");

        List<Ticket> tickets = ticketDAO.saveAll(List.of(ticket1, ticket2));
        assertEquals(2, tickets.size(), "There should be 2 tickets saved");
    }

    @Test
    public void testSaveNullTicket() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketDAO.save(null));
        assertEquals("ticket can not be null", exception.getMessage());
    }

    @Test
    public void testSaveInvalidTicket() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketDAO.save(ticket));
        assertEquals("ghost tickets are not allowed", exception.getMessage());
    }

    @Test
    public void testSaveDuplicateTicket() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID());
        ticket.setFromLocation("A");
        ticket.setToLocation("B");
        ticket.setPricePaid(100.0);
        ticket.setSeatId(UUID.randomUUID());
        ticket.setUserEmail("user@example.com");

        ticketDAO.save(ticket);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketDAO.save(ticket));
        assertEquals("ticket already exists", exception.getMessage());
    }

    @Test
    public void testGetTicketBySeatIdWithNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketDAO.getTicketBySeatId(null));
        assertEquals("seatId can not be null", exception.getMessage());
    }

    @Test
    public void testGetTicketByUserWithNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> ticketDAO.getTicketByUser(null));
        assertEquals("email can not be null when finding ticket", exception.getMessage());
    }
}
