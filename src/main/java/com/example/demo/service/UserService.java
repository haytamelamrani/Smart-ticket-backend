// src/main/java/com/example/demo/service/UserService.java
package com.example.demo.service;

import com.example.demo.dto.UpdateUserRoleDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LogService logService;

    // 🔍 1. Lister tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🔁 2. Modifier le rôle d’un utilisateur
    public boolean updateUserRole(UpdateUserRoleDto dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(dto.getRole());
            userRepository.save(user);
            logService.log(user, "UPDATE_ROLE_TO_" + dto.getRole().name());
            return true;
        }
        return false;
    }

    // ❌ 3. Supprimer un utilisateur
    public boolean deleteUserById(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    public void updateSpecialite(Long userId, String specialite) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setSpecialite(specialite);
        userRepository.save(user);
    }

}
