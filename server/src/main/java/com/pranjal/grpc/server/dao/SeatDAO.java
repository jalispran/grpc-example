package com.pranjal.grpc.server.dao;

import com.pranjal.grpc.server.entity.Seat;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

@Repository
public class SeatDAO {
    private static final String SEAT_SECTION_A = "A";
    private static final String SEAT_SECTION_B = "B";
    private static final int SEATS_PER_SECTION = 20;
    private final Map<UUID, Seat> seats = new HashMap<>();
    private final Vector<String> aSecSeatNumbers = new Vector<>();
    private final Vector<String> bSecSeatNumbers = new Vector<>();

    public Optional<Seat> getById(UUID seatId) {
        if (seats.containsKey(seatId)) {
            return Optional.of(seats.get(seatId));
        }
        return Optional.empty();
    }

    public Collection<Seat> getAll() {
        return seats.values();
    }

    public Seat save(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("seat can not be null");
        }
        if (!StringUtils.hasText(seat.getSeatSection())
                || seat.getSeatNumber() == null
                || !StringUtils.hasText(seat.getUserEmail())) {
            throw new IllegalArgumentException("ghost seats are not allowed");
        }
        if (seat.getSeatId() == null) {
            seat.setSeatId(UUID.randomUUID());
        }
        if (seats.containsKey(seat.getSeatId())) {
            throw new IllegalArgumentException("seat already exists");
        }
        if (SEAT_SECTION_A.equals(seat.getSeatSection())
                && !aSecSeatNumbers.contains(seat.getSeatNumber())) {
            aSecSeatNumbers.add(Integer.parseInt(seat.getSeatNumber()), seat.getSeatNumber());
        } else if (SEAT_SECTION_B.equals(seat.getSeatSection())
                && !bSecSeatNumbers.contains(seat.getSeatNumber())) {
            bSecSeatNumbers.add(Integer.parseInt(seat.getSeatNumber()), seat.getSeatNumber());
        } else {
            throw new IllegalArgumentException("Seat number: " + seat.getSeatNumber() + " not available in section: " + seat.getSeatSection());
        }
        seats.put(seat.getSeatId(), seat);
        return seat;
    }

    public List<Seat> saveAll(List<Seat> seatList) {
        return seatList.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    public Optional<Seat> getAvailableSeat() {
        Optional<Seat> seatA = findFirstEmptySeatNumber(aSecSeatNumbers);
        if (seatA.isPresent()) {
            seatA.get().setSeatSection(SEAT_SECTION_A);
            return seatA;
        }

        Optional<Seat> seatB = findFirstEmptySeatNumber(bSecSeatNumbers);
        if (seatB.isPresent()) {
            seatB.get().setSeatSection(SEAT_SECTION_B);
            return seatB;
        }
        return Optional.empty();
    }

    public Optional<Seat> findFirstEmptySeatNumber(Vector<String> seatSection) {
        for (int i = 0; i < seatSection.size() || seatSection.size() < SEATS_PER_SECTION; i++) {
            if (seatSection.size() == i || seatSection.get(i) == null) {
                return  Optional.of(Seat.builder()
                        .seatNumber(String.valueOf(i))
                        .build());
            }
        }
        return Optional.empty();
    }

    public List<String> getSections() {
        return List.of(SEAT_SECTION_A, SEAT_SECTION_B);
    }

    public Seat deleteByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("email can not be empty");
        }
        Seat seat = seats.values().stream()
                .filter(s -> email.equals(s.getUserEmail()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("seat not found for user"));
        seats.remove(seat.getSeatId());
        if (SEAT_SECTION_A.equals(seat.getSeatSection())) {
            aSecSeatNumbers.add(Integer.parseInt(seat.getSeatNumber()), null);
        } else if (SEAT_SECTION_B.equals(seat.getSeatSection())) {
            bSecSeatNumbers.add(Integer.parseInt(seat.getSeatNumber()), null);
        }
        return seat;
    }

}
