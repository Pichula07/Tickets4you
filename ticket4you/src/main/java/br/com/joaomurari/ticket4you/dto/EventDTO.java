package br.com.joaomurari.ticket4you.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDTO {
    private String id;
    private String eventName;
    private String dateTime;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
}