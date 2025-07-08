package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("otp");
        return ResponseEntity.ok(authService.verifyOtp(email, code));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> result = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (result.containsKey("error")) {
            return ResponseEntity.status(401).body(result);
        }

        return ResponseEntity.ok(result);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        return ResponseEntity.ok(authService.requestPasswordReset(email));
    }

    @PostMapping("/reset-password")
        public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            System.out.println("🔧 Requête reçue pour reset-password avec token: " + request.getToken());
            String result = authService.resetPasswordByToken(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("❌ Exception lors du reset-password : " + e.getMessage());
            e.printStackTrace(); // pour avoir le stack trace complet
            return ResponseEntity.status(500).body("❌ Erreur interne lors de la réinitialisation du mot de passe");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("❌ Token manquant ou invalide.");
        }

        String token = authHeader.substring(7); // Supprime "Bearer "
        String result = authService.logout(token);
        return ResponseEntity.ok(result);
    }


}
