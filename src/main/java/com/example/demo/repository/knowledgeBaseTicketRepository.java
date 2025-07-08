package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KnowledgeBaseTicket;

public interface knowledgeBaseTicketRepository extends JpaRepository<KnowledgeBaseTicket, Long> {
}
