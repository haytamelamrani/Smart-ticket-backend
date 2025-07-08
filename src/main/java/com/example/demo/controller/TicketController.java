package com.example.demo.controller;

import com.example.demo.dto.SimilarTicketDto;
import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.dto.TicketWithMessagesDTO;
import com.example.demo.service.TicketService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<TicketResponseDto> createTicket(
        @ModelAttribute TicketRequestDto dto
    ) {
        return ResponseEntity.ok(ticketService.createTicket(dto));
    }
    @GetMapping("/with-messages")
    public ResponseEntity<List<TicketWithMessagesDTO>> getTicketsWithMessages() {
        return ResponseEntity.ok(ticketService.getAllTicketsWithMessages());
    }

    @PatchMapping("/{ticketId}/assign-user/{userId}")
    public ResponseEntity<?> assignTicketToUser(@PathVariable Long ticketId, @PathVariable Long userId) {
        ticketService.assignTicketToUser(ticketId, userId);
        return ResponseEntity.ok("🎯 Ticket assigné à l'utilisateur.");
    }

    @PatchMapping("/{ticketId}/assign-team/{teamId}")
    public ResponseEntity<?> assignTicketToTeam(@PathVariable Long ticketId, @PathVariable Long teamId) {
        ticketService.assignTicketToTeam(ticketId, teamId);
        return ResponseEntity.ok("👥 Ticket assigné à l'équipe.");
    }
    @PostMapping("/{ticketId}/etat")
    public ResponseEntity<?> updateTicketEtat(
            @PathVariable Long ticketId,
            @RequestParam String nouvelEtat,
            @RequestParam String userEmail,
            @RequestParam(required = false) String solution
    ) {
        ticketService.updateTicketEtat(ticketId, nouvelEtat, userEmail,solution);
        return ResponseEntity.ok("✅ État du ticket mis à jour avec succès.");
    }
    
    @PostMapping("/{ticketId}/confirmer")
    public ResponseEntity<?> confirmerClotureTicket(
            @PathVariable Long ticketId,
            @RequestParam String userEmail,
            @RequestParam(required = false) String feedback,
            @RequestParam(required = false) Integer note
    ) {
        ticketService.confirmerClotureTicket(ticketId, userEmail, feedback, note);
        return ResponseEntity.ok("✅ Clôture du ticket confirmée par le client.");
    }

    @GetMapping("/grouped")
    public ResponseEntity<List<List<SimilarTicketDto>>> getGroupedTickets(@RequestParam double threshold) {
        return ResponseEntity.ok(ticketService.groupSimilarTickets(threshold));
    }

    @PostMapping("/{id}/fermer-par-client")
    public ResponseEntity<?> fermerEtArchiverParClient(
            @PathVariable Long id,
            @RequestParam String email) {

        ticketService.fermerTicketParClientEtArchiver(id, email);
        return ResponseEntity.ok("✅ Ticket archivé avec succès.");
    }
}
