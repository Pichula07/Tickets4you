package br.com.joaomurari.event4you.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketCheckResponseDTO {

    private String eventId;
    private boolean hasTickets;

    public TicketCheckResponseDTO(boolean hasTickets) {
        this.hasTickets = hasTickets;
    }

}
