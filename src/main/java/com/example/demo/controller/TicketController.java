package com.example.demo.controller;

import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.dto.TicketWithMessagesDTO;
import com.example.demo.entity.KnowledgeBaseTicket;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.User;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.knowledgeBaseTicketRepository;
import com.example.demo.service.TicketService;
import com.example.demo.service.TicketSimilarityService;
import com.example.demo.util.TicketVectorizer;

import java.util.Map;
import java.util.HashMap;
import java.util.List;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.mapstruct.control.MappingControl.Use;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final TicketSimilarityService similarityService;
    private final knowledgeBaseTicketRepository knowledgeBaseRepository;
    private final TicketVectorizer ticketVectorizer;
    private final UserRepository userRepository;

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

    @GetMapping("/Historique")
    public ResponseEntity<List<TicketWithMessagesDTO>> getAllTicketsWithMessagesHistorique() {
        return ResponseEntity.ok(ticketService.getAllTicketsWithMessagesHistorique());
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


    @PostMapping("/{id}/fermer-par-client")
    public ResponseEntity<?> fermerEtArchiverParClient(
            @PathVariable Long id,
            @RequestParam String email) {

        ticketService.fermerTicketParClientEtArchiver(id, email);
        return ResponseEntity.ok("✅ Ticket archivé avec succès.");
    }

    @PutMapping("/{id}/reveiller")
    public ResponseEntity<?> reveillerTicket(@PathVariable Long id) {
        try {
            ticketService.reveillerTicket(id);
            return ResponseEntity.ok("Ticket réveillé avec succès.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket non trouvé.");
        }
    }

    @GetMapping("/groupes")
    public Map<Integer, List<Ticket>> getGroupes(@RequestParam double seuil) {
        ticketVectorizer.loadModel("models/word2vec_model.zip");
        List<Ticket> ticketslList = ticketRepository.findAll();
        return similarityService.grouperTicketsParSimilarite(ticketslList, seuil);
    }

    @PostMapping("/solution")
    public ResponseEntity<?> getSolution(@RequestBody Ticket ticket, @RequestParam double seuil) {
        List<KnowledgeBaseTicket> kb = knowledgeBaseRepository.findAll();
        return similarityService.proposerSolution(ticket, kb, seuil)
                .map(sol -> ResponseEntity.ok(Map.of("solution", sol)))
                .orElse(ResponseEntity.ok(Map.of("solution", "Aucune solution trouvée")));
    }

    
    @GetMapping("/with-messages-Specialite/{email}")
    public ResponseEntity<?> getMatchingTicketsWithMessages(@PathVariable String email) {
        User agent = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Agent introuvable"));

        String specialite = agent.getSpecialite();
        List<TicketWithMessagesDTO> tickets = ticketService.getAllTicketsWithMessagesBySpecialite(specialite);
        return ResponseEntity.ok(tickets);
    }


    @GetMapping("/with-messages-Assigned/{email}")
    public ResponseEntity<?> getAllTicketsForAgent(@PathVariable String email) {
        User agent = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Agent introuvable"));

        List<TicketWithMessagesDTO> tickets = ticketService.getAllTicketsWithMessagesAssigned(agent);
        return ResponseEntity.ok(tickets);
                
    }

    @PostMapping("/{id}/update-category")
    public ResponseEntity<?> updateTicketCategory(@PathVariable Long id, @RequestParam String category) {
        try {
            Ticket updatedTicket = ticketService.updateCategory(id, category);
            return ResponseEntity.ok(updatedTicket);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket non trouvé.");
        }
    }


}
