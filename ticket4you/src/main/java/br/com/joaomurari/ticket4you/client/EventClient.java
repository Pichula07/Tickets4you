package br.com.joaomurari.ticket4you.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.joaomurari.ticket4you.dto.EventDTO;
import br.com.joaomurari.ticket4you.dto.TicketCheckResponseDTO;

@FeignClient(name = "event4you", url = "http://localhost:8081/api/events/v1")
public interface EventClient {
    @GetMapping("/{id}")
    EventDTO getEventById(@PathVariable("id") String id);

    @GetMapping("/check-tickets-by-event/{eventId}")
    TicketCheckResponseDTO checkTicketsByEvent(@PathVariable("eventId") String eventId);
}