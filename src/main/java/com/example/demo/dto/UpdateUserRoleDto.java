// src/main/java/com/example/demo/dto/UpdateUserRoleDto.java
package com.example.demo.dto;

import com.example.demo.entity.UserRole;
import lombok.Data;

@Data
public class UpdateUserRoleDto {
    private String email;
    private UserRole role;
}
