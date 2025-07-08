package com.example.demo.dto;

public class SimilarTicketDto {
    private Long id;
    private String title;
    private String description;
    private String userEmail;
    private String etat;
    private String priority;
    private String category;
    private String type;
    private double similarity; // 🎯 Pourcentage

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    public String getEtat() {
        return etat;
    }
    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public double getSimilarity() {
        return similarity;
    }
    public String getType() {
        return type;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

}
