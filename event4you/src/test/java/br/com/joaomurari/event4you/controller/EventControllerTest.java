package br.com.joaomurari.event4you.controller;

import br.com.joaomurari.event4you.dto.ZipCodeResponse;
import br.com.joaomurari.event4you.model.Event;
import br.com.joaomurari.event4you.service.EventService;
import br.com.joaomurari.event4you.client.ZipCodeClient;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private ZipCodeClient zipCodeClient;

    @InjectMocks
    private EventController eventController;

    private Event event;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new Event();
        event.setEventName("Test Event");
        event.setZipCode("12345");
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent_WhenValid() {
        ZipCodeResponse zipCodeResponse = new ZipCodeResponse();
        zipCodeResponse.setStreet("Street");
        zipCodeResponse.setNeighborhood("Neighborhood");
        zipCodeResponse.setCity("City");
        zipCodeResponse.setState("State");

        when(zipCodeClient.getZipCodeInfo("12345")).thenReturn(zipCodeResponse);
        when(eventService.create(any(Event.class))).thenReturn(event);

        ResponseEntity<Event> response = eventController.createEvent(event);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        verify(zipCodeClient, times(1)).getZipCodeInfo("12345");
        verify(eventService, times(1)).create(any(Event.class));
    }

    @Test
    void createEvent_ShouldThrowBadRequest_WhenZipCodeNotFound() {
        when(zipCodeClient.getZipCodeInfo("12345")).thenThrow(FeignException.NotFound.class);

        assertThrows(ResponseStatusException.class, () -> {
            eventController.createEvent(event);
        });
    }

    @Test
    void getAll_ShouldReturnListOfEvents() {
        List<Event> events = List.of(event);
        when(eventService.getAll()).thenReturn(events);

        ResponseEntity<List<Event>> response = eventController.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getById_ShouldReturnEvent_WhenValidId() {
        when(eventService.getById("1")).thenReturn(event);

        ResponseEntity<Event> response = eventController.getById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(event.getEventName(), response.getBody().getEventName());
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent_WhenValid() {
        ZipCodeResponse zipCodeResponse = new ZipCodeResponse("Street", "Neighborhood", "City", "State");
        when(zipCodeClient.getZipCodeInfo("12345")).thenReturn(zipCodeResponse);
        when(eventService.update("1", event)).thenReturn(event);

        ResponseEntity<Event> response = eventController.updateEvent("1", event);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(zipCodeClient, times(1)).getZipCodeInfo("12345");
        verify(eventService, times(1)).update("1", event);
    }

    @Test
    void updateEvent_ShouldThrowBadRequest_WhenZipCodeNotFound() {
        when(zipCodeClient.getZipCodeInfo("12345")).thenThrow(FeignException.NotFound.class);

        assertThrows(ResponseStatusException.class, () -> {
            eventController.updateEvent("1", event);
        });
    }

    @Test
    void deleteEvent_ShouldReturnNoContent_WhenDeleted() {
        when(eventService.getById("1")).thenReturn(event);
        doNothing().when(eventService).deleteEvent("1");

        ResponseEntity<?> response = eventController.deleteEvent("1");

        assertEquals(204, response.getStatusCodeValue());
        verify(eventService, times(1)).deleteEvent("1");
    }

}
