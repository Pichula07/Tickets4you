package br.com.joaomurari.event4you.service;

import br.com.joaomurari.event4you.client.TicketClient;
import br.com.joaomurari.event4you.dto.TicketCheckResponseDTO;
import br.com.joaomurari.event4you.exception.ConflictException;
import br.com.joaomurari.event4you.exception.ResourceNotFound;
import br.com.joaomurari.event4you.model.Event;
import br.com.joaomurari.event4you.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketClient ticketClient;

    @InjectMocks
    private EventService eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new Event();
        event.setId("1");
        event.setEventName("Test Event");
    }

    @Test
    void create_ShouldReturnSavedEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.create(event);

        assertNotNull(createdEvent);
        assertEquals(event.getEventName(), createdEvent.getEventName());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void getById_ShouldReturnEvent_WhenEventExists() {
        when(eventRepository.findById("1")).thenReturn(Optional.of(event));

        Event foundEvent = eventService.getById("1");

        assertNotNull(foundEvent);
        assertEquals(event.getId(), foundEvent.getId());
        verify(eventRepository, times(1)).findById("1");
    }

    @Test
    void getById_ShouldThrowResourceNotFound_WhenEventDoesNotExist() {
        when(eventRepository.findById("invalidId")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> eventService.getById("invalidId"));
    }

    @Test
    void update_ShouldThrowResourceNotFound_WhenEventDoesNotExist() {
        Event updatedEvent = new Event();
        updatedEvent.setEventName("Updated Event");

        when(eventRepository.findById("invalidId")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> eventService.update("invalidId", updatedEvent));
    }

    @Test
    void hasSoldTickets_ShouldReturnTrue_WhenTicketsExist() {
        TicketCheckResponseDTO ticketCheckResponse = new TicketCheckResponseDTO(true);
        when(ticketClient.checkTicketsByEvent("1")).thenReturn(ticketCheckResponse);

        boolean result = eventService.hasSoldTickets("1");

        assertTrue(result);
        verify(ticketClient, times(1)).checkTicketsByEvent("1");
    }

    @Test
    void hasSoldTickets_ShouldReturnFalse_WhenNoTicketsSold() {
        TicketCheckResponseDTO ticketCheckResponse = new TicketCheckResponseDTO(false);
        when(ticketClient.checkTicketsByEvent("1")).thenReturn(ticketCheckResponse);

        boolean result = eventService.hasSoldTickets("1");

        assertFalse(result);
        verify(ticketClient, times(1)).checkTicketsByEvent("1");
    }

    @Test
    void hasSoldTickets_ShouldThrowConflictException_WhenTicketCheckFails() {
        when(ticketClient.checkTicketsByEvent("1")).thenThrow(new RuntimeException("Error"));

        assertThrows(ConflictException.class, () -> eventService.hasSoldTickets("1"));
    }

    @Test
    void deleteEvent_ShouldDeleteEvent_WhenNoTicketsSold() {
        when(eventRepository.findById("1")).thenReturn(Optional.of(event));
        when(ticketClient.checkTicketsByEvent("1")).thenReturn(new TicketCheckResponseDTO(false));

        eventService.deleteEvent("1");

        verify(eventRepository, times(1)).findById("1");
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void deleteEvent_ShouldThrowConflict_WhenTicketsSold() {
        when(ticketClient.checkTicketsByEvent("1")).thenReturn(new TicketCheckResponseDTO("1", true));

        assertThrows(ConflictException.class, () -> {
            eventService.deleteEvent("1");
        });

        verify(ticketClient, times(1)).checkTicketsByEvent("1");
        verify(eventRepository, never()).findById(anyString());
    }

    @Test
    void deleteEvent_ShouldReturnDeletedEvent_WhenNoTicketsSold() {
        when(ticketClient.checkTicketsByEvent("1")).thenReturn(new TicketCheckResponseDTO("1", false));

        when(eventRepository.findById("1")).thenReturn(Optional.of(event));

        eventService.deleteEvent("1");

        verify(eventRepository, times(1)).findById("1");
        verify(eventRepository, times(1)).save(any(Event.class));
        assertTrue(event.isDeleted());
    }
}