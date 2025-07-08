package com.example.demo.dto;

import java.time.LocalDateTime;

public class MessageDto {

    private Long ticketId;
    private String content;
    private String senderType;
    private String senderId;
    private String channel;
    private String status;
    private LocalDateTime timestamp;

    // Getters
    public Long getTicketId() {
        return ticketId;
    }

    public String getContent() {
        return content;
    }

    public String getSenderType() {
        return senderType;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getChannel() {
        return channel;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
