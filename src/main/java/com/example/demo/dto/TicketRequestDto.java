package com.example.demo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class TicketRequestDto {
    private String title;
    private String description;
    private String category;
    private String priority;
    private String type;
    private String userEmail;
    private String etat;
    private List<MultipartFile> attachments;

    
}
