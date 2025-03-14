package br.com.joaomurari.event4you.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.joaomurari.event4you.client.ZipCodeClient;
import br.com.joaomurari.event4you.dto.ZipCodeResponse;
import br.com.joaomurari.event4you.model.Event;
import br.com.joaomurari.event4you.service.EventService;
import br.com.joaomurari.event4you.exception.ConflictException;
import feign.FeignException;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events/v1")
public class EventController {

    private final EventService service;
    private final ZipCodeClient zipCodeClient;

    @PostMapping("/create-event")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            ZipCodeResponse zipCodeResponse = zipCodeClient.getZipCodeInfo(event.getZipCode());
            event.setStreet(zipCodeResponse.getStreet());
            event.setNeighborhood(zipCodeResponse.getNeighborhood());
            event.setCity(zipCodeResponse.getCity());
            event.setState(zipCodeResponse.getState());

            Event createdEvent = service.create(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);

        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP not found", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        List<Event> events = service.getAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") String id) {
        Event event = service.getById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Event>> getAllSorted() {
        List<Event> sortedEvents = service.getAllSorted();
        return ResponseEntity.ok(sortedEvents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody Event updatedEvent) {
        try {
            ZipCodeResponse zipCodeResponse = zipCodeClient.getZipCodeInfo(updatedEvent.getZipCode());
            updatedEvent.setStreet(zipCodeResponse.getStreet());
            updatedEvent.setNeighborhood(zipCodeResponse.getNeighborhood());
            updatedEvent.setCity(zipCodeResponse.getCity());
            updatedEvent.setState(zipCodeResponse.getState());

            Event updated = service.update(id, updatedEvent);
            return ResponseEntity.ok(updated);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP not found", e);
        }
    }

    @DeleteMapping("/delete-event/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        try {
            Event event = service.getById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }

            service.deleteEvent(id);
            return ResponseEntity.noContent().build();

        } catch (ConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Event cannot be deleted because it has tickets that have been sold", e);
        }
    }

}
