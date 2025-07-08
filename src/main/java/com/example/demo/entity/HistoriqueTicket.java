package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_ticket") // facultatif mais recommandé
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 1000)
    private String description;

    private String category;
    private String priority;
    private String type;

    private String userEmail;
    private String etat;
    private LocalDateTime etatUpdatedAt;
    private LocalDateTime createdAt;

    private String clientFeedback;
    private Integer clientRating;

    private Long assignedToId;
    private Long assignedTeamId;
}
