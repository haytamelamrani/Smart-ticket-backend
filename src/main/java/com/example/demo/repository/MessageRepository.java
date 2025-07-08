package com.example.demo.repository;

import com.example.demo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Pour récupérer tous les messages d’un ticket, triés par date
    List<Message> findByTicketIdOrderByTimestampAsc(Long ticketId);
    List<Message> findByTicketId(Long ticketId);
}
