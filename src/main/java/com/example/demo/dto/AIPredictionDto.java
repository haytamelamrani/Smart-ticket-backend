package com.example.demo.dto;

import lombok.Data;

@Data
public class AIPredictionDto {
    private String category;
    private String priority;
    private String type;
    private String suggestion;
}
