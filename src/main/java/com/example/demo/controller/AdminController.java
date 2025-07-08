package com.example.demo.controller;

import com.example.demo.dto.AdminDashboardDto;
import com.example.demo.dto.UpdateUserRoleDto;
import com.example.demo.dto.UserAdminDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DashboardService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final DashboardService dashboardService;

    //  1. Récupérer tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDto>> getAllUsers() {
        List<UserAdminDto> users = userRepository.findAll().stream()
            .map(user -> new UserAdminDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getCompany(),
                user.isVerified(),
                user.getLastLoginAt()
            ))
            .toList();

        return ResponseEntity.ok(users);
    }

    //  2. Modifier le rôle d’un utilisateur
    @PutMapping("/users/role")
    public ResponseEntity<String> updateUserRole(@RequestBody UpdateUserRoleDto dto) {
        boolean updated = userService.updateUserRole(dto);
        if (updated) {
            return ResponseEntity.ok("✅ Rôle mis à jour");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Utilisateur non trouvé");
        }
    }

    //  3. Supprimer un utilisateur
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.ok("✅ Utilisateur supprimé");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Utilisateur introuvable");
        }
    }

    //  4. Dashboard admin (statistiques globales)
    @GetMapping("/dashboard")
    public AdminDashboardDto getAdminDashboard() {
        System.out.println("👤 Rôles de l'utilisateur connecté : " +
            SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        return dashboardService.getAdminDashboard();
    }
}
