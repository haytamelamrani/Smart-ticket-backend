package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 5000)
    private String content;
    


    private String senderType; // "user", "agent", "ia"
    private String senderId;   // optionnel (pour l’agent)

    private String channel;    // "ia" ou "agent"

    private String status;     // "sent", "received", "read" — peut être null pour IA

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
}
