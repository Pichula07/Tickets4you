package br.com.joaomurari.ticket4you.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDTO {
    private String id;
    private String customerName;
    private String cpf;
    private String customerEmail;
    private EventDTO event;
    private String brlTotalAmount;
    private String usdTotalAmount;
    private String status;
}