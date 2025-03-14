package br.com.joaomurari.ticket4you.service;

import java.util.HashMap;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.joaomurari.ticket4you.client.EventClient;
import br.com.joaomurari.ticket4you.dto.CreateTicketDTO;
import br.com.joaomurari.ticket4you.dto.EventDTO;
import br.com.joaomurari.ticket4you.dto.TicketResponseDTO;
import br.com.joaomurari.ticket4you.exception.TicketNotFoundException;
import br.com.joaomurari.ticket4you.model.Ticket;
import br.com.joaomurari.ticket4you.repository.TicketRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventClient eventClient;

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public TicketResponseDTO createTicket(CreateTicketDTO dto) {
        EventDTO event = eventClient.getEventById(dto.getEventId());

        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID().toString())
                .customerName(dto.getCustomerName())
                .cpf(dto.getCpf())
                .customerEmail(dto.getCustomerEmail())
                .brlAmount(dto.getBrlAmount())
                .usdAmount(dto.getUsdAmount())
                .deleted(false)
                .build();

        ticket = ticketRepository.save(ticket);

        return buildTicketResponse(ticket, event);
    }

    public TicketResponseDTO updateTicket(String ticketId, CreateTicketDTO dto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + ticketId));

        EventDTO event = eventClient.getEventById(dto.getEventId());

        ticket.setCustomerName(dto.getCustomerName());
        ticket.setCpf(dto.getCpf());
        ticket.setCustomerEmail(dto.getCustomerEmail());
        ticket.setEventId(dto.getEventId());
        ticket.setBrlAmount(dto.getBrlAmount());
        ticket.setUsdAmount(dto.getUsdAmount());

        ticketRepository.save(ticket);

        return buildTicketResponse(ticket, event);
    }

    public TicketResponseDTO getTicketById(String id) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);

        if (ticketOpt.isEmpty()) {
            throw new TicketNotFoundException("Ticket not found with ID: " + id);
        }

        Ticket ticket = ticketOpt.get();

        EventDTO event = eventClient.getEventById(ticket.getEventId());

        return buildTicketResponse(ticket, event);
    }

    public List<TicketResponseDTO> getTicketsByCpf(String cpf) {
        List<Ticket> tickets = ticketRepository.findByCpf(cpf);

        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for CPF: " + cpf);
        }

        return tickets.stream()
                .map(ticket -> {
                    EventDTO event = eventClient.getEventById(ticket.getEventId());
                    return buildTicketResponse(ticket, event);
                })
                .toList();
    }

    @Transactional
    public void deleteTicketByCpf(String cpf) {
        List<Ticket> tickets = ticketRepository.findByCpf(cpf);

        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for CPF: " + cpf);
        }

        ticketRepository.deleteAll(tickets);
    }

    public boolean hasSoldTickets(String eventId) {
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        return !tickets.isEmpty();
    }

    public void deleteTicket(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with ID: " + ticketId));

        ticketRepository.delete(ticket);
    }

    public TicketResponseDTO buildTicketResponse(Ticket ticket, EventDTO event) {
        TicketResponseDTO response = new TicketResponseDTO();
        response.setId(ticket.getId());
        response.setCustomerName(ticket.getCustomerName());
        response.setCpf(ticket.getCpf());
        response.setCustomerEmail(ticket.getCustomerEmail());
        response.setEvent(event);
        response.setBrlTotalAmount("R$ " + ticket.getBrlAmount());
        response.setUsdTotalAmount("USD " + ticket.getUsdAmount());
        response.setStatus("concluído");
        return response;
    }
}
