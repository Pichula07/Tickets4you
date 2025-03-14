package br.com.joaomurari.ticket4you.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateTicketDTO {
    private String customerName;
    private String cpf;
    private String customerEmail;
    private String eventId;
    private String brlAmount;
    private String usdAmount;
}