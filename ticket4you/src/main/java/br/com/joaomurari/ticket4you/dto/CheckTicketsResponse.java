package br.com.joaomurari.ticket4you.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckTicketsResponse {
    private String eventId;
    private boolean hasTickets;
    private String message;

    public CheckTicketsResponse(String eventId, boolean hasTickets, String message) {
        this.eventId = eventId;
        this.hasTickets = hasTickets;
        this.message = message;
    }
}