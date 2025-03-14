package br.com.joaomurari.event4you.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.joaomurari.event4you.client.TicketClient;
import br.com.joaomurari.event4you.dto.TicketCheckResponseDTO;
import br.com.joaomurari.event4you.model.Event;
import br.com.joaomurari.event4you.repository.EventRepository;
import br.com.joaomurari.event4you.exception.ConflictException;
import br.com.joaomurari.event4you.exception.ResourceNotFound;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository repository;
    private final TicketClient ticketClient;

    public Event create(Event event) {
        return repository.save(event);
    }

    public List<Event> getAll() {
        return repository.findAll();
    }

    public Event getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Event not found with id: " + id));
    }

    public List<Event> getAllSorted() {
        List<Event> events = repository.findAll();
        return events.stream()
                .sorted((e1, e2) -> {
                    String eventName1 = e1.getEventName();
                    String eventName2 = e2.getEventName();

                    if (eventName1 == null && eventName2 == null)
                        return 0;
                    if (eventName1 == null)
                        return 1;
                    if (eventName2 == null)
                        return -1;

                    return eventName1.compareToIgnoreCase(eventName2);
                })
                .collect(Collectors.toList());
    }

    public Event update(String id, Event updatedEvent) {
        return repository.findById(id).map(event -> {
            event.setEventName(updatedEvent.getEventName());
            event.setDateTime(updatedEvent.getDateTime());
            event.setZipCode(updatedEvent.getZipCode());
            event.setStreet(updatedEvent.getStreet());
            event.setNeighborhood(updatedEvent.getNeighborhood());
            event.setCity(updatedEvent.getCity());
            event.setState(updatedEvent.getState());
            return repository.save(event);
        }).orElseThrow(() -> new ResourceNotFound("Event not found with id: " + id));
    }

    public boolean hasSoldTickets(String eventId) {
        try {
            TicketCheckResponseDTO response = ticketClient.checkTicketsByEvent(eventId);
            return response.isHasTickets();
        } catch (Exception e) {
            throw new ConflictException("Error while checking tickets for event: " + eventId);
        }
    }

    public void deleteEvent(String id) {

        if (hasSoldTickets(id)) {
            throw new ConflictException("Event cannot be deleted because it has tickets that have been sold");
        }

        Event event = repository.findById(id).orElseThrow(() -> new ResourceNotFound("Event not found"));
        event.setDeleted(true);
        repository.save(event);
    }

}
