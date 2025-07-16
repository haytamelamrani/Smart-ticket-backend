package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgentStatsDto {
    private Long agentId;
    private String agentName;
    private String email;
    private String specialite;
    private Double averageRating;
    private Long ticketCount;
}
