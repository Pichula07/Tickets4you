package br.com.joaomurari.ticket4you.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.joaomurari.ticket4you.model.Ticket;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    List<Ticket> findByCpf(String cpf);

    List<Ticket> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

}