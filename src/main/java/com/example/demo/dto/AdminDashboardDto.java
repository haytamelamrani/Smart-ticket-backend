package com.example.demo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AdminDashboardDto {

    //  Nombre total de tickets
    private long totalTickets;

    //  Tickets par état (NOUVEAU, EN_COURS, CLOS...)
    private Map<String, Long> ticketsByEtat;

    //  Tickets par jour (YYYY-MM-DD : nb)
    private Map<String, Long> ticketsByDay;

    //  Nombre total d’utilisateurs (clients + agents + admins)
    private long totalUsers;

    //  Nombre d’agents actifs (ayant traité au moins un ticket)
    private long activeAgents;

    //  Nombre de tickets non assignés
    private long unassignedTickets;

    //  Temps moyen de traitement (en jours)
    private double avgResolutionTime;

    //  Nombre de tickets par priorité (HAUTE, MOYENNE, BASSE)
    private Map<String, Long> ticketsByPriority;

    //  Répartition des tickets par type (Incident, Demande, etc.)
    private Map<String, Long> ticketsByType;

    //  Moyenne des notes données par les clients
    private double avgClientRating;
}
