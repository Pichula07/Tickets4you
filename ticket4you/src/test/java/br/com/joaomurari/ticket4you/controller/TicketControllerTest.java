package br.com.joaomurari.ticket4you.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.joaomurari.ticket4you.dto.CreateTicketDTO;
import br.com.joaomurari.ticket4you.dto.TicketResponseDTO;
import br.com.joaomurari.ticket4you.exception.TicketNotFoundException;
import br.com.joaomurari.ticket4you.service.TicketService;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @BeforeAll
    static void setUp() {
        System.setProperty("EVENT_API", "localhost:8081");
        System.setProperty("TICKET_API", "localhost:8082");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateTicket() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO();
        dto.setEventId("eventId");
        dto.setCustomerName("João M");
        dto.setCpf("12345678900");
        dto.setCustomerEmail("joaocompass@hotmail.com");
        dto.setBrlAmount("100.0");
        dto.setUsdAmount("20.0");

        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setId("ticketId");
        responseDTO.setCustomerName("João M");

        when(ticketService.createTicket(any(CreateTicketDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/tickets/v1/create-ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ticketId"))
                .andExpect(jsonPath("$.customerName").value("João M"));
    }

    @Test
    public void testGetTicketById() throws Exception {
        String ticketId = "ticketId";
        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setId(ticketId);
        responseDTO.setCustomerName("João M");

        when(ticketService.getTicketById(ticketId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/tickets/v1/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.customerName").value("João M"));
    }

    @Test
    public void testGetTicketById_NotFound() throws Exception {
        String ticketId = "invalidId";
        when(ticketService.getTicketById(ticketId)).thenThrow(new TicketNotFoundException("Ticket not found"));

        mockMvc.perform(get("/api/tickets/v1/{ticketId}", ticketId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTicketsByCpf() throws Exception {
        String cpf = "12345678900";
        TicketResponseDTO responseDTO = new TicketResponseDTO();
        responseDTO.setId("ticketId");
        responseDTO.setCustomerName("João M");

        when(ticketService.getTicketsByCpf(cpf)).thenReturn(Collections.singletonList(responseDTO));

        mockMvc.perform(get("/api/tickets/v1/get-ticket-by-cpf/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("ticketId"))
                .andExpect(jsonPath("$[0].customerName").value("João M"));
    }

    @Test
    public void testGetTicketsByCpf_NotFound() throws Exception {
        String cpf = "invalidCpf";
        when(ticketService.getTicketsByCpf(cpf)).thenThrow(new TicketNotFoundException("No tickets found"));

        mockMvc.perform(get("/api/tickets/v1/get-ticket-by-cpf/{cpf}", cpf))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTicket() throws Exception {
        String ticketId = "ticketId";
        doNothing().when(ticketService).deleteTicket(ticketId);

        mockMvc.perform(delete("/api/tickets/v1/delete-ticket/{ticketId}", ticketId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteTicket_NotFound() throws Exception {
        String ticketId = "invalidId";
        doThrow(new TicketNotFoundException("Ticket not found")).when(ticketService).deleteTicket(ticketId);

        mockMvc.perform(delete("/api/tickets/v1/delete-ticket/{ticketId}", ticketId))
                .andExpect(status().isNotFound());
    }
}
