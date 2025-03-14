package br.com.joaomurari.event4you.model;

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
@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String eventName;
    private String dateTime;
    private String zipCode;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    @JsonIgnore
    private boolean deleted;

}