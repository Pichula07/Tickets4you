package br.com.joaomurari.event4you.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.joaomurari.event4you.dto.TicketCheckResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ticket4you", url = "http://${TICKET_API}/api/tickets/v1")
public interface TicketClient {

    @GetMapping("/check-tickets/{eventId}")
    TicketCheckResponseDTO checkTicketsByEvent(@PathVariable String eventId);

}
