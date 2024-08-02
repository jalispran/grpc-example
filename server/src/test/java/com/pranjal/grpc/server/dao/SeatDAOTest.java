package com.pranjal.grpc.server.dao;

import com.pranjal.grpc.server.entity.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SeatDAOTest {

    private SeatDAO seatDAO;

    @BeforeEach
    public void setUp() {
        seatDAO = new SeatDAO();
    }

    @Test
    public void testGetById_SeatExists() {
        UUID seatId = UUID.randomUUID();
        Seat seat = Seat.builder()
                .seatId(seatId)
                .seatSection("A")
                .seatNumber("0")
                .userEmail("test@example.com")
                .build();
        seatDAO.save(seat);

        Optional<Seat> result = seatDAO.getById(seatId);
        assertTrue(result.isPresent());
        assertEquals(seat, result.get());
    }

    @Test
    public void testGetById_SeatNotExists() {
        UUID seatId = UUID.randomUUID();
        Optional<Seat> result = seatDAO.getById(seatId);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetAll() {
        Seat seat1 = Seat.builder()
                .seatId(UUID.randomUUID())
                .seatSection("A")
                .seatNumber("0")
                .userEmail("test1@example.com")
                .build();
        Seat seat2 = Seat.builder()
                .seatId(UUID.randomUUID())
                .seatSection("B")
                .seatNumber("0")
                .userEmail("test2@example.com")
                .build();
        seatDAO.save(seat1);
        seatDAO.save(seat2);

        Collection<Seat> seats = seatDAO.getAll();
        assertEquals(2, seats.size());
    }

    @Test
    public void testSave_ValidSeat() {
        Seat seat = Seat.builder()
                .seatId(UUID.randomUUID())
                .seatSection("A")
                .seatNumber("0")
                .userEmail("test@example.com")
                .build();
        Seat savedSeat = seatDAO.save(seat);

        assertEquals(seat, savedSeat);
        assertTrue(seatDAO.getById(seat.getSeatId()).isPresent());
    }

    @Test
    public void testSave_SeatAlreadyExists() {
        UUID seatId = UUID.randomUUID();
        Seat seat = Seat.builder()
                .seatId(seatId)
                .seatSection("A")
                .seatNumber("0")
                .userEmail("test@example.com")
                .build();
        seatDAO.save(seat);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> seatDAO.save(seat));
        assertEquals("seat already exists", exception.getMessage());
    }

    @Test
    public void testSave_NullSeat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> seatDAO.save(null));
        assertEquals("seat can not be null", exception.getMessage());
    }

    @Test
    public void testSave_InvalidSeat() {
        Seat seat = Seat.builder()
                .seatId(UUID.randomUUID())
                .seatSection("A")
                .seatNumber(null)
                .userEmail("test@example.com")
                .build();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> seatDAO.save(seat));
        assertEquals("ghost seats are not allowed", exception.getMessage());
    }

    @Test
    public void testGetAvailableSeat_SeatAvailable() {
        Seat seat = Seat.builder()
                .seatId(UUID.randomUUID())
                .seatSection("A")
                .seatNumber("0")
                .userEmail("test@example.com")
                .build();
        seatDAO.save(seat);

        Optional<Seat> availableSeat = seatDAO.getAvailableSeat();
        assertTrue(availableSeat.isPresent());
        assertEquals("A", availableSeat.get().getSeatSection());
    }

    @Test
    public void testGetAvailableSeat_NoSeatAvailable() {
        for (int i = 0; i < 20; i++) {
            Seat seat = Seat.builder()
                    .seatId(UUID.randomUUID())
                    .seatSection("A")
                    .seatNumber(String.valueOf(i))
                    .userEmail("test" + i + "@example.com")
                    .build();
            seatDAO.save(seat);
        }

        Optional<Seat> availableSeat = seatDAO.getAvailableSeat();
        assertTrue(availableSeat.isPresent());
        assertEquals("B", availableSeat.get().getSeatSection());
    }

    @Test
    public void testDeleteByEmail_SeatExists() {
        Seat seat = Seat.builder()
                .seatId(UUID.randomUUID())
                .seatSection("A")
                .seatNumber("0")
                .userEmail("test@example.com")
                .build();
        seatDAO.save(seat);

        Seat deletedSeat = seatDAO.deleteByEmail("test@example.com");
        assertEquals(seat, deletedSeat);
        assertFalse(seatDAO.getById(seat.getSeatId()).isPresent());
    }

    @Test
    public void testDeleteByEmail_SeatNotExists() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> seatDAO.deleteByEmail("test@example.com"));
        assertEquals("seat not found for user", exception.getMessage());
    }

    @Test
    public void testDeleteByEmail_NullEmail() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> seatDAO.deleteByEmail(null));
        assertEquals("email can not be empty", exception.getMessage());
    }

    @Test
    public void testDeleteByEmail_EmptyEmail() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> seatDAO.deleteByEmail(""));
        assertEquals("email can not be empty", exception.getMessage());
    }
}
