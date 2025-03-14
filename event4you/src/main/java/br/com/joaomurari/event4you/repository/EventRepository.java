package br.com.joaomurari.event4you.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.joaomurari.event4you.model.Event;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    @Query("{ 'id' : ?0 }")
    void updateDeletedById(String id);

    List<Event> findByDeletedFalse();

    Optional<Event> findByIdAndDeletedFalse(String id);
}
