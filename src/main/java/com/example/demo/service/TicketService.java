package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.DashboardStatsDto;
import com.example.demo.dto.SimilarTicketDto;
import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.dto.TicketWithMessagesDTO;
import com.example.demo.entity.Ticket;

import io.micrometer.common.lang.Nullable;

public interface TicketService {
    TicketResponseDto createTicket(TicketRequestDto dto);
    List<TicketWithMessagesDTO> getAllTicketsWithMessages();
    void assignTicketToUser(Long ticketId, Long userId);
    void assignTicketToTeam(Long ticketId, Long teamId);
    void confirmerClotureTicket(Long ticketId, String userEmail, String feedback, Integer note);
    DashboardStatsDto getDashboardStats();
    List<List<SimilarTicketDto>> groupSimilarTickets(double threshold);
    SimilarTicketDto toSimilarDto(Ticket ticket, double similarity);
    void verifierEtArchiverSiNecessaire(Ticket ticket);
    void fermerTicketParClientEtArchiver(Long ticketId, String email);
    void updateTicketEtat(Long ticketId, String nouvelEtat, String userEmail, @Nullable String solution);
}
