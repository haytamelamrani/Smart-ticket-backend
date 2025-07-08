package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TicketWithMessagesDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String category;
    private String priority;
    private String type;    
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime etatUpdatedAt;


    private List<MessageDto> agentMessages;
    private List<MessageDto> aiMessages;

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public String getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public List<MessageDto> getAgentMessages() {
        return agentMessages;
    }

    public List<MessageDto> getAiMessages() {
        return aiMessages;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAgentMessages(List<MessageDto> agentMessages) {
        this.agentMessages = agentMessages;
    }

    public void setAiMessages(List<MessageDto> aiMessages) {
        this.aiMessages = aiMessages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getEtatUpdatedAt() {
        return etatUpdatedAt;
    }

    public void setEtatUpdatedAt(LocalDateTime etatUpdatedAt) {
        this.etatUpdatedAt = etatUpdatedAt;
    }
}
