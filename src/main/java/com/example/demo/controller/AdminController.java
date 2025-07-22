package com.example.demo.controller;

import com.example.demo.dto.AdminDashboardDto;
import com.example.demo.dto.AgentStatsDto;
import com.example.demo.dto.UpdateUserRoleDto;
import com.example.demo.dto.UserAdminDto;
import com.example.demo.entity.Ticket;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DashboardService;
import com.example.demo.service.TicketService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final DashboardService dashboardService;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    //  1. Récupérer tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDto>> getAllUsers() {
        List<UserAdminDto> users = userRepository.findAll().stream()
            .map(user -> new UserAdminDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getCompany(),
                user.isVerified(),
                user.getLastLoginAt(),
                user.getSpecialite()
            ))
            .toList();

        return ResponseEntity.ok(users);
    }

    //  2. Modifier le rôle d’un utilisateur
    @PutMapping("/users/role")
    public ResponseEntity<String> updateUserRole(@RequestBody UpdateUserRoleDto dto) {
        boolean updated = userService.updateUserRole(dto);
        if (updated) {
            return ResponseEntity.ok("✅ Rôle mis à jour");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Utilisateur non trouvé");
        }
    }

    //  3. Supprimer un utilisateur
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.ok("✅ Utilisateur supprimé");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Utilisateur introuvable");
        }
    }

    //  4. Dashboard admin (statistiques globales)
    @GetMapping("/dashboard")
    public AdminDashboardDto getAdminDashboard() {
        System.out.println("👤 Rôles de l'utilisateur connecté : " +
            SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        return dashboardService.getAdminDashboard();
    }

    @PutMapping("/users/{id}/specialite")
    public ResponseEntity<?> updateSpecialite(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        System.out.println("test");
        String nouvelleSpecialite = request.get("specialite");
        userService.updateSpecialite(id, nouvelleSpecialite);
        return ResponseEntity.ok("Spécialité mise à jour");
    }

    @GetMapping("/agents/stats")
    public ResponseEntity<List<AgentStatsDto>> getAgentStats() {
        return ResponseEntity.ok(ticketService.getAllAgentStats());
    }


    @GetMapping("/tickets")
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @PutMapping("/tickets/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody Ticket updatedTicket) {
        Optional<Ticket> optional = ticketRepository.findById(id);
        if (optional.isEmpty()) return ResponseEntity.notFound().build();

        Ticket ticket = optional.get();
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setDescription(updatedTicket.getDescription());
        ticket.setPriority(updatedTicket.getPriority());
        ticket.setType(updatedTicket.getType());
        ticket.setEtat(updatedTicket.getEtat());

        return ResponseEntity.ok(ticketRepository.save(ticket));
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        if (!ticketRepository.existsById(id)) return ResponseEntity.notFound().build();
        ticketRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
