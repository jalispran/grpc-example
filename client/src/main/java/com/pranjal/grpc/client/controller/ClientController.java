package com.pranjal.grpc.client.controller;

import com.pranjal.grpc.client.dto.input.ModifyUserSeatInput;
import com.pranjal.grpc.client.dto.input.PurchaseTicketInput;
import com.pranjal.grpc.client.dto.output.PurchaseTicketOutput;
import com.pranjal.grpc.client.dto.output.UserSeatOutput;
import com.pranjal.grpc.client.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {

    private final TicketService ticketService;

    @PostMapping(value = "/ticket/purchase", consumes = "application/json")
    public ResponseEntity<?> purchaseTicket(@RequestBody @Valid PurchaseTicketInput input) {
        PurchaseTicketOutput response = ticketService.purchaseTicket(input.toPurchaseTicketRequest());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getReceipt(@PathVariable String email) {
        PurchaseTicketOutput response = ticketService.getReceipt(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/seats")
    public ResponseEntity<?> getSeatsBySection(@RequestParam String sectionName) {
        List<UserSeatOutput> seatsBySection = ticketService.getSeatsBySection(sectionName);
        return new ResponseEntity<>(seatsBySection, HttpStatus.OK);
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> removeUser(@PathVariable String email) {
        String response = ticketService.removeUser(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/user/modify")
    public ResponseEntity<?> modifySeatOfUser(@RequestBody @Valid ModifyUserSeatInput input) {
        PurchaseTicketOutput response = ticketService.modifyUserSeat(input);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
