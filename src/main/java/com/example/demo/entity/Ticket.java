package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

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

     //  Champ pour savoir si le client a confirmé la clôture
     private boolean confirmedByClient;

     //  Avis/Note du client à la fin
    private String clientFeedback;
    private Integer clientRating;

    //  Utilisateur à qui le ticket est assigné (agent ou admin)
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    // Assignation optionnelle à des utilisateurs
    @ManyToMany
    @JoinTable(
        name = "ticket_assigned_users",
        joinColumns = @JoinColumn(name = "ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> assignedUsers = new ArrayList<>();

    // Assignation optionnelle à une équipe
    @ManyToOne
    @JoinColumn(name = "assigned_team_id")
    private Team assignedTeam;
    
}
