package com.example.demo.service.impl;

import com.example.demo.dto.AdminDashboardDto;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    public AdminDashboardDto getAdminDashboard() {
        AdminDashboardDto dto = new AdminDashboardDto();

        // ✅ Total des tickets
        dto.setTotalTickets(ticketRepository.count());

        // 📈 Tickets par état
        Map<String, Long> byEtat = new HashMap<>();
        for (Object[] row : ticketRepository.countByEtatGroup()) {
            String etat = (String) row[0];
            Long count = (Long) row[1];
            byEtat.put(etat, count);
        }
        dto.setTicketsByEtat(byEtat);

        // 📅 Tickets par jour
        Map<String, Long> byDay = new HashMap<>();
        for (Object[] row : ticketRepository.countByDay()) {
            String day = (String) row[0];
            Long count = (Long) row[1];
            byDay.put(day, count);
        }
        dto.setTicketsByDay(byDay);

        // 🧍 Nombre total d'utilisateurs
        dto.setTotalUsers(userRepository.count());

        // 🧑‍💻 Nombre d'agents actifs
        dto.setActiveAgents(userRepository.countActiveAgents());

        // 🗂️ Tickets non assignés
        dto.setUnassignedTickets(ticketRepository.countUnassigned());

        // 🕒 Temps moyen de traitement (en jours)
        Double avgDurationInHours = ticketRepository.avgResolutionTimeInHours();
        if (avgDurationInHours != null) {
            double avgDays = Math.round((avgDurationInHours / 24.0) * 10.0) / 10.0;
            dto.setAvgResolutionTime(avgDays);
        } else {
            dto.setAvgResolutionTime(0.0);
        }

        // 🧾 Tickets par priorité
        Map<String, Long> byPriority = new HashMap<>();
        for (Object[] row : ticketRepository.countGroupByPriority()) {
            String priority = (String) row[0];
            Long count = (Long) row[1];
            byPriority.put(priority, count);
        }
        dto.setTicketsByPriority(byPriority);

        // 🌍 Tickets par type
        Map<String, Long> byType = new HashMap<>();
        for (Object[] row : ticketRepository.countGroupByType()) {
            String type = (String) row[0];
            Long count = (Long) row[1];
            byType.put(type, count);
        }
        dto.setTicketsByType(byType);

        // ⭐ Note moyenne des clients
        Double avgRating = ticketRepository.averageClientRating();
        dto.setAvgClientRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);

        return dto;
    }
}
