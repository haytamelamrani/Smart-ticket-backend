package com.example.demo.dto;

import java.time.LocalDateTime;

public record UserAdminDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    String role,
    String company,
    Boolean isVerified,
    LocalDateTime lastLoginAt,
    String specialite
) {}
