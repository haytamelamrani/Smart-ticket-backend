package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDto {
    private String ticketId;
    private String message;
    private String etat;
    private LocalDateTime createdAt;
}
