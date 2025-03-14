package br.com.joaomurari.ticket4you.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.joaomurari.ticket4you.client.EventClient;
import br.com.joaomurari.ticket4you.dto.CreateTicketDTO;
import br.com.joaomurari.ticket4you.dto.EventDTO;
import br.com.joaomurari.ticket4you.dto.TicketResponseDTO;
import br.com.joaomurari.ticket4you.exception.TicketNotFoundException;
import br.com.joaomurari.ticket4you.model.Ticket;
import br.com.joaomurari.ticket4you.repository.TicketRepository;
import br.com.joaomurari.ticket4you.service.TicketService;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventClient eventClient;

    @InjectMocks
    private TicketService ticketService;

    @Test
    public void testGetAll() {
        Ticket ticket1 = new Ticket();
        Ticket ticket2 = new Ticket();
        List<Ticket> ticketList = Arrays.asList(ticket1, ticket2);

        when(ticketRepository.findAll()).thenReturn(ticketList);
        List<Ticket> result = ticketService.getAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    public void testCreateTicket() {
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("John Doe");
        dto.setCpf("12345678900");
        dto.setCustomerMail("johndoe@example.com");
        dto.setBrlAmount("100.0");
        dto.setUsdAmount("20.0");

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("eventId");
        eventDTO.setEventName("Event 1");

        when(eventClient.getEventById(dto.getEventId())).thenReturn(eventDTO);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponseDTO result = ticketService.createTicket(dto);
        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("12345678900", result.getCpf());
        assertEquals("johndoe@example.com", result.getCustomerMail());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    public void testCreateTicket_EventNotFound() {
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("John Doe");
        dto.setCpf("12345678900");
        dto.setCustomerMail("johndoe@example.com");
        dto.setBrlAmount("100.0");
        dto.setUsdAmount("20.0");

        when(eventClient.getEventById(dto.getEventId())).thenThrow(new RuntimeException("Event not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.createTicket(dto);
        });

        assertEquals("Event not found", exception.getMessage());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    public void testCreateTicket_SaveTicketFailed() {
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("John Doe");
        dto.setCpf("12345678900");
        dto.setCustomerMail("johndoe@example.com");
        dto.setBrlAmount("100.0");
        dto.setUsdAmount("20.0");

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("eventId");
        eventDTO.setEventName("Event 1");

        when(eventClient.getEventById(dto.getEventId())).thenReturn(eventDTO);

        when(ticketRepository.save(any(Ticket.class))).thenThrow(new RuntimeException("Failed to save ticket"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.createTicket(dto);
        });

        assertEquals("Failed to save ticket", exception.getMessage());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    public void testUpdateTicket() {
        String ticketId = "ticketId";
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("Jane Doe");
        dto.setCpf("09876543210");
        dto.setCustomerMail("janedoe@example.com");
        dto.setBrlAmount("150.0");
        dto.setUsdAmount("30.0");

        Ticket existingTicket = new Ticket();
        existingTicket.setId(ticketId);
        existingTicket.setCustomerName("Old Name");

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("eventId");
        eventDTO.setEventName("Updated Event");

        when(ticketRepository.findById(ticketId)).thenReturn(java.util.Optional.of(existingTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponseDTO result = ticketService.updateTicket(ticketId, dto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getCustomerName());
        assertEquals("09876543210", result.getCpf());
        assertEquals("janedoe@example.com", result.getCustomerMail());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    public void testUpdateTicketNotFound() {
        String ticketId = "ticketId";
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("Jane Doe");

        when(ticketRepository.findById(ticketId)).thenReturn(java.util.Optional.empty());

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.updateTicket(ticketId, dto);
        });

        assertEquals("Ticket not found with ID: " + ticketId, exception.getMessage());
    }

}
