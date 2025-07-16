package com.example.demo.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String company;
    //  Champs pour OTP :
    private String otpCode;
    private LocalDateTime otpGeneratedAt;
    private boolean isVerified;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private LocalDateTime lastLoginAt;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String specialite; // Exemple : "réseau", "logiciel", etc.

    public String getSpecialite() {
        return specialite;
    }

    // ✅ Setter
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}
