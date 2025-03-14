package br.com.joaomurari.ticket4you.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;
    private String customerName;
    private String cpf;
    private String customerEmail;
    private String eventId;
    private String brlAmount;
    private String usdAmount;
    @JsonIgnore
    private boolean deleted;

}