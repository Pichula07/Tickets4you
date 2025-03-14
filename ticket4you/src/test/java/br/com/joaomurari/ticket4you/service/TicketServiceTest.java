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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        dto.setCustomerName("João M");
        dto.setCpf("12345678900");
        dto.setCustomerEmail("joaocompass@hotmail.com");
        dto.setBrlAmount("100.0");
        dto.setUsdAmount("20.0");

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("eventId");
        eventDTO.setEventName("Event 1");

        when(eventClient.getEventById(dto.getEventId())).thenReturn(eventDTO);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponseDTO result = ticketService.createTicket(dto);
        assertNotNull(result);
        assertEquals("João M", result.getCustomerName());
        assertEquals("12345678900", result.getCpf());
        assertEquals("joaocompass@hotmail.com", result.getCustomerEmail());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    public void testCreateTicket_EventNotFound() {
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("João M");
        dto.setCpf("12345678900");
        dto.setCustomerEmail("joaocompass@hotmail.com");
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
        dto.setCustomerName("João M");
        dto.setCpf("12345678900");
        dto.setCustomerEmail("joaocompass@hotmail.com");
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
        dto.setCustomerName("João M");
        dto.setCpf("12345678900");
        dto.setCustomerEmail("joaocompass@hotmail.com");
        dto.setBrlAmount("150.0");
        dto.setUsdAmount("30.0");

        Ticket existingTicket = new Ticket();
        existingTicket.setId(ticketId);
        existingTicket.setCustomerName("Old Name");

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("eventId");
        eventDTO.setEventName("Updated Event");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponseDTO result = ticketService.updateTicket(ticketId, dto);

        assertNotNull(result);
        assertEquals("João M", result.getCustomerName());
        assertEquals("12345678900", result.getCpf());
        assertEquals("joaocompass@hotmail.com", result.getCustomerEmail());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    public void testUpdateTicketNotFound() {
        String ticketId = "ticketId";
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("João M");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.updateTicket(ticketId, dto);
        });

        assertEquals("Ticket not found with ID: " + ticketId, exception.getMessage());
    }

    @Test
    public void testGetTicketById() {
        String ticketId = "ticketId";
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setCustomerName("João M");

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("eventId");
        eventDTO.setEventName("Event 1");

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(eventClient.getEventById(ticket.getEventId())).thenReturn(eventDTO);

        TicketResponseDTO result = ticketService.getTicketById(ticketId);
        assertNotNull(result);
        assertEquals(ticketId, result.getId());
        assertEquals("João M", result.getCustomerName());
    }

    @Test
    public void testGetTicketById_NotFound() {
        String ticketId = "invalidId";
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.getTicketById(ticketId);
        });

        assertEquals("Ticket not found with ID: " + ticketId, exception.getMessage());
    }

    @Test
    public void testGetTicketsByCpf() {
        String cpf = "12345678900";
        Ticket ticket = new Ticket();
        ticket.setId("ticketId");
        ticket.setCpf(cpf);
        ticket.setCustomerName("João M");

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId("eventId");
        eventDTO.setEventName("Event 1");

        when(ticketRepository.findByCpf(cpf)).thenReturn(Collections.singletonList(ticket));
        when(eventClient.getEventById(ticket.getEventId())).thenReturn(eventDTO);

        List<TicketResponseDTO> result = ticketService.getTicketsByCpf(cpf);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("João M", result.get(0).getCustomerName());
    }

    @Test
    public void testGetTicketsByCpf_NotFound() {
        String cpf = "invalidCpf";
        when(ticketRepository.findByCpf(cpf)).thenReturn(Collections.emptyList());

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.getTicketsByCpf(cpf);
        });

        assertEquals("No tickets found for CPF: " + cpf, exception.getMessage());
    }

    @Test
    public void testCheckTicketsByEvent() {
        String eventId = "eventId";
        when(ticketRepository.existsByEventId(eventId)).thenReturn(true);

        boolean result = ticketService.checkTicketsByEvent(eventId);
        assertTrue(result);
    }

    @Test
    public void testCheckTicketsByEvent_NoTickets() {
        String eventId = "eventId";
        when(ticketRepository.existsByEventId(eventId)).thenReturn(false);

        boolean result = ticketService.checkTicketsByEvent(eventId);
        assertFalse(result);
    }

    @Test
    public void testDeleteTicket() {
        String ticketId = "ticketId";
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        ticketService.deleteTicket(ticketId);
        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    public void testDeleteTicket_NotFound() {
        String ticketId = "invalidId";
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.deleteTicket(ticketId);
        });

        assertEquals("Ticket not found with ID: " + ticketId, exception.getMessage());
    }

    @Test
    public void testDeleteTicketByCpf() {
        String cpf = "12345678900";
        Ticket ticket = new Ticket();
        ticket.setId("ticketId");
        ticket.setCpf(cpf);

        when(ticketRepository.findByCpf(cpf)).thenReturn(Collections.singletonList(ticket));

        ticketService.deleteTicketByCpf(cpf);
        verify(ticketRepository, times(1)).deleteAll(Collections.singletonList(ticket));
    }

    @Test
    public void testDeleteTicketByCpf_NotFound() {
        String cpf = "invalidCpf";
        when(ticketRepository.findByCpf(cpf)).thenReturn(Collections.emptyList());

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketService.deleteTicketByCpf(cpf);
        });

        assertEquals("No tickets found for CPF: " + cpf, exception.getMessage());
    }
}
