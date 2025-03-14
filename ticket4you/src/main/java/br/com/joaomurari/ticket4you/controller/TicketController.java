package br.com.joaomurari.ticket4you.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.joaomurari.ticket4you.dto.CheckTicketsResponse;
import br.com.joaomurari.ticket4you.dto.CreateTicketDTO;
import br.com.joaomurari.ticket4you.dto.TicketResponseDTO;
import br.com.joaomurari.ticket4you.model.Ticket;
import br.com.joaomurari.ticket4you.service.TicketService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/tickets/v1")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService service;

    @PostMapping("/create-ticket")
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody CreateTicketDTO dto) {
        TicketResponseDTO ticket = service.createTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/check-tickets/{eventId}")
    public ResponseEntity<?> checkTicketsByEvent(@PathVariable String eventId) {
        boolean hasTickets = service.hasSoldTickets(eventId);

        if (hasTickets) {
            return ResponseEntity.ok(new CheckTicketsResponse(eventId, true, "Tickets sold for this event."));
        } else {
            return ResponseEntity.ok(new CheckTicketsResponse(eventId, false, "No tickets sold for this event."));
        }
    }

    @PutMapping("/update-ticket/{ticketId}")
    public ResponseEntity<TicketResponseDTO> updateTicket(@PathVariable String ticketId,
            @RequestBody CreateTicketDTO dto) {
        TicketResponseDTO updatedTicket = service.updateTicket(ticketId, dto);
        return ResponseEntity.ok(updatedTicket);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable String ticketId) {
        TicketResponseDTO ticket = service.getTicketById(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/get-ticket-by-cpf/{cpf}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByCpf(@PathVariable String cpf) {
        List<TicketResponseDTO> tickets = service.getTicketsByCpf(cpf);
        return ResponseEntity.ok(tickets);
    }

    @DeleteMapping("/delete-ticket-by-cpf/{cpf}")
    public ResponseEntity<Void> deleteTicketByCpf(@PathVariable String cpf) {
        service.deleteTicketByCpf(cpf);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-ticket/{ticketId}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable String ticketId) {
        service.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}