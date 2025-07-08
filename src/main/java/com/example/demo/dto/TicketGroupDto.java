package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketGroupDto {
    private Long id;
    private String title;
    private String description;
    private double similarity; // en pourcentage (0.0 à 1.0)
}
