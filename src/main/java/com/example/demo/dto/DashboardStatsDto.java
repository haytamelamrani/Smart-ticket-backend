package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DashboardStatsDto {
    private long totalTickets;
    private long openTickets;
    private long totalUsers;
    private List<StatCount> ticketsByStatus;
    private List<StatCount> ticketsByDay;

    @Data
    @AllArgsConstructor
    public static class StatCount {
        private String name; // nom ou date
        private long value;  // count
    }
}
