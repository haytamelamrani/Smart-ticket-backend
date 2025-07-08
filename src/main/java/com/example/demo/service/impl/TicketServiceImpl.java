package com.example.demo.service.impl;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.dto.MessageDto;
import com.example.demo.dto.SimilarTicketDto;
import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.dto.TicketWithMessagesDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.TicketMapper;
import com.example.demo.repository.*;
import com.example.demo.service.TicketService;
import com.example.demo.util.SimilarityUtil;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HistoriqueTicketRepository historiqueTicketRepository;
    private final knowledgeBaseTicketRepository knowledgeBaseTicketRepository;

    @Override
    public TicketResponseDto createTicket(TicketRequestDto dto) {
        System.out.println("🎯 Début de création du ticket");

        System.out.println("📝 Reçu : " + dto);
        Ticket ticket = ticketMapper.toEntity(dto);
        System.out.println("🔄 Ticket mappé : " + ticket);

        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setEtat("NOUVEAU");

        try {
            Ticket saved = ticketRepository.save(ticket);
            System.out.println("✅ Ticket sauvegardé en base : " + saved.getId());

            if (dto.getAttachments() != null) {
                for (MultipartFile file : dto.getAttachments()) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            String fileName = file.getOriginalFilename();
                            byte[] content = file.getBytes();
                            String uploadDir = "uploads/";

                            File directory = new File(uploadDir);
                            if (!directory.exists()) {
                                directory.mkdirs();
                            }

                            File destinationFile = new File(uploadDir + fileName);
                            try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
                                fos.write(content);
                                System.out.println("📁 Fichier sauvegardé dans : " + destinationFile.getAbsolutePath());
                            } catch (IOException e) {
                                System.err.println("⚠️ Erreur sauvegarde fichier : " + e.getMessage());
                            }
                        } catch (IOException e) {
                            System.err.println("⚠️ Erreur lecture fichier joint : " + e.getMessage());
                        }
                    }
                }
            }

            TicketResponseDto response = ticketMapper.toDto(saved);
            System.out.println("📤 Réponse envoyée : " + response);
            return response;

        } catch (Exception ex) {
            System.err.println("❌ Erreur interne lors de la création du ticket : " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Erreur lors de la création du ticket");
        }
    }

    @Override
    public List<TicketWithMessagesDTO> getAllTicketsWithMessages() {
        List<Ticket> tickets = ticketRepository.findAll();
        List<TicketWithMessagesDTO> result = new ArrayList<>();

        for (Ticket ticket : tickets) {
            List<Message> messages = messageRepository.findByTicketId(ticket.getId());

            List<MessageDto> agentMessages = new ArrayList<>();
            List<MessageDto> aiMessages = new ArrayList<>();

            for (Message message : messages) {
                if (message.getContent() == null || message.getContent().isBlank()) {
                    continue;
                }

                MessageDto dto = new MessageDto();
                dto.setTicketId(ticket.getId());
                dto.setContent(message.getContent());
                dto.setSenderType(message.getSenderType());
                dto.setSenderId(message.getSenderId());
                dto.setChannel(message.getChannel());
                dto.setStatus(message.getStatus());
                dto.setTimestamp(message.getTimestamp());

                if ("agent".equalsIgnoreCase(message.getChannel())) {
                    agentMessages.add(dto);
                } else if ("ai".equalsIgnoreCase(message.getChannel())) {
                    aiMessages.add(dto);
                }
            }

            TicketWithMessagesDTO dto = new TicketWithMessagesDTO();
            dto.setId(ticket.getId());
            dto.setTitle(ticket.getTitle());
            dto.setDescription(ticket.getDescription());
            dto.setStatus(ticket.getEtat());
            dto.setCategory(ticket.getCategory());
            dto.setPriority(ticket.getPriority());
            dto.setType(ticket.getType());
            dto.setEmail(ticket.getUserEmail());
            dto.setAgentMessages(agentMessages);
            dto.setAiMessages(aiMessages);
            dto.setCreatedAt(ticket.getCreatedAt());
            dto.setEtatUpdatedAt(ticket.getEtatUpdatedAt());
            result.add(dto);
        }

        return result;
    }

    // ✅ Méthode pour assigner un ticket à un utilisateur
    @Override
    public void assignTicketToUser(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        ticket.getAssignedUsers().add(user);
        ticketRepository.save(ticket);
        System.out.println("👤 Ticket " + ticketId + " assigné à l'utilisateur " + userId);
    }

    // ✅ Méthode pour assigner un ticket à une équipe
    @Override
    public void assignTicketToTeam(Long ticketId, Long teamId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Équipe introuvable"));

        ticket.setAssignedTeam(team);
        ticketRepository.save(ticket);
        System.out.println("👥 Ticket " + ticketId + " assigné à l'équipe " + teamId);
    }

    @Override
    public void updateTicketEtat(Long ticketId, String nouvelEtat, String userEmail, @Nullable String solution) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Si l'état devient "EN_COURS", assigner le ticket à l'utilisateur qui modifie
        if ("EN_COURS".equalsIgnoreCase(nouvelEtat)) {
            ticket.setAssignedTo(user);
        }

        // Si l'état devient "CLOS", enregistrer le ticket dans la base de connaissances
        if ("CLOS".equalsIgnoreCase(nouvelEtat) && solution != null && !solution.trim().isEmpty()) {
            KnowledgeBaseTicket kbTicket = KnowledgeBaseTicket.builder()
                    .title(ticket.getTitle())
                    .description(ticket.getDescription())
                    .solution(solution)
                    .build();

            knowledgeBaseTicketRepository.save(kbTicket);
        }

        ticket.setEtat(nouvelEtat);
        ticket.setEtatUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }


    @Override
    public void confirmerClotureTicket(Long ticketId, String userEmail, String feedback, Integer note) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));

        if (!ticket.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à confirmer ce ticket.");
        }

        ticket.setConfirmedByClient(true);
        ticket.setClientFeedback(feedback);
        ticket.setClientRating(note);
        ticket.setEtat("CLOS");
        ticket.setEtatUpdatedAt(LocalDateTime.now());

        ticketRepository.save(ticket);

        verifierEtArchiverSiNecessaire(ticket);
    }

    @Override
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto dto = new DashboardStatsDto();

        // ✅ Initialiser les listes vides pour éviter le null
        dto.setTicketsByStatus(new ArrayList<>());
        dto.setTicketsByDay(new ArrayList<>());
        // ✅ Remplir les données
        long totalTickets = ticketRepository.count();
        long openTickets = ticketRepository.countByEtat("NOUVEAU");
        long totalUsers = userRepository.count();

        List<Object[]> statusStats = ticketRepository.countByEtatGroup();
        if (statusStats != null) {
            List<DashboardStatsDto.StatCount> ticketsByStatus = statusStats.stream()
                .map(row -> new DashboardStatsDto.StatCount((String) row[0], (Long) row[1]))
                .toList();
            dto.setTicketsByStatus(ticketsByStatus);
        }

        List<Object[]> dayStats = ticketRepository.countByDay();
        if (dayStats != null) {
            List<DashboardStatsDto.StatCount> ticketsByDay = dayStats.stream()
                .map(row -> new DashboardStatsDto.StatCount(row[0].toString(), (Long) row[1]))
                .toList();
            dto.setTicketsByDay(ticketsByDay);
        }

        dto.setTotalTickets(totalTickets);
        dto.setOpenTickets(openTickets);
        dto.setTotalUsers(totalUsers);

        return dto;
    }

    @Override
    public List<List<SimilarTicketDto>> groupSimilarTickets(double threshold) {
        List<Ticket> allTickets = ticketRepository.findAll();
        Set<Long> processed = new HashSet<>();
        List<List<SimilarTicketDto>> groups = new ArrayList<>();
    
        for (Ticket ticket : allTickets) {
            if (processed.contains(ticket.getId())) continue;
    
            List<SimilarTicketDto> group = new ArrayList<>();
    
            // 🎯 Ajouter le ticket principal avec similarité = 1.0
            group.add(toSimilarDto(ticket, 1.0));
            processed.add(ticket.getId());
    
            for (Ticket other : allTickets) {
                if (!ticket.getId().equals(other.getId()) && !processed.contains(other.getId())) {
                    double similarity = SimilarityUtil.computeCosineSimilarity(
                        ticket.getTitle() + " " + ticket.getDescription(),
                        other.getTitle() + " " + other.getDescription()
                    );
    
                    if (similarity >= threshold) {
                        group.add(toSimilarDto(other, similarity));
                        processed.add(other.getId());
                    }
                }
            }
    
            // ✅ Ajouter le groupe uniquement s’il contient des similaires
            if (group.size() > 1) {
                groups.add(group);
            }
        }
    
        return groups;
    }
    
    public SimilarTicketDto toSimilarDto(Ticket ticket, double similarity) {
        SimilarTicketDto dto = new SimilarTicketDto();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setUserEmail(ticket.getUserEmail());
        dto.setEtat(ticket.getEtat());
        dto.setPriority(ticket.getPriority());
        dto.setCategory(ticket.getCategory());
        dto.setType(ticket.getType());
        dto.setSimilarity(similarity);
        return dto;
    }    
    
    @Transactional
    public void verifierEtArchiverSiNecessaire(Ticket ticket) {
        if ("CLOS".equals(ticket.getEtat()) && ticket.isConfirmedByClient()) {
            HistoriqueTicket histo = HistoriqueTicket.builder()
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .type(ticket.getType())
                .userEmail(ticket.getUserEmail())
                .etat(ticket.getEtat())
                .etatUpdatedAt(ticket.getEtatUpdatedAt())
                .createdAt(ticket.getCreatedAt())
                .clientFeedback(ticket.getClientFeedback())
                .clientRating(ticket.getClientRating())
                .assignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null)
                .assignedTeamId(ticket.getAssignedTeam() != null ? ticket.getAssignedTeam().getId() : null)
                .build();

            historiqueTicketRepository.save(histo);
            ticketRepository.delete(ticket);
        }
    }


    @Override
    @Transactional
    public void fermerTicketParClientEtArchiver(Long ticketId, String email) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket introuvable"));

        if (!ticket.getUserEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Vous n’êtes pas autorisé à fermer ce ticket.");
        }

        ticket.setEtat("CLOS");
        ticket.setConfirmedByClient(true);
        ticket.setEtatUpdatedAt(LocalDateTime.now());


        // 🧠 Appel de ta méthode existante
        verifierEtArchiverSiNecessaire(ticket);
    }
   
}