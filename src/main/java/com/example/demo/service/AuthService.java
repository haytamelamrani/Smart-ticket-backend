package com.example.demo.service;

import com.example.demo.config.JwtService;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LogService logService;
    private final ConcurrentHashMap<String, TempUser> pendingUsers = new ConcurrentHashMap<>();

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "❌ Cet email est déjà utilisé.";
        }

        String otpCode = generateOtpCode();
        TempUser tempUser = new TempUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getCompany(),
                otpCode,
                LocalDateTime.now()
        );

        pendingUsers.put(request.getEmail(), tempUser);

        if (sendEmail(request.getEmail(), "Code de vérification - Smart Ticket",
                "Voici votre code de vérification : " + otpCode + "\nValide 10 minutes.")) {
            return "✅ Code envoyé. Vérifiez votre email.";
        } else {
            pendingUsers.remove(request.getEmail());
            return "⚠️ Échec de l'envoi de l'email.";
        }
    }

    public String verifyOtp(String email, String code) {
        TempUser tempUser = pendingUsers.get(email);
        if (tempUser == null) return "❌ Aucun utilisateur en attente.";
        if (!tempUser.otpCode().equals(code)) return "❌ Code incorrect.";
        if (tempUser.otpGeneratedAt().plusMinutes(10).isBefore(LocalDateTime.now()))
            return "⏰ Code expiré.";

        User user = User.builder()
                .firstName(tempUser.firstName())
                .lastName(tempUser.lastName())
                .email(tempUser.email())
                .password(tempUser.password())
                .company(tempUser.company())
                .isVerified(true)
                .role(UserRole.CLIENT)
                .build();

        userRepository.save(user);
        logService.log(user, "REGISTER");
        pendingUsers.remove(email);

        return "✅ Compte vérifié avec succès.";
    }

    public Map<String, String> login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return Map.of("error", "❌ Utilisateur non trouvé.");

        User user = userOpt.get();
        if (!user.isVerified()) return Map.of("error", "⚠️ Compte non vérifié.");

        if (!passwordEncoder.matches(rawPassword, user.getPassword()))
            return Map.of("error", "❌ Mot de passe incorrect.");

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        logService.log(user, "LOGIN");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return Map.of("message", "✅ Connexion réussie", "token", token,"role", user.getRole().name(),"email", user.getEmail());
    }

    public String requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return "❌ Email introuvable.";

        String resetToken = UUID.randomUUID().toString();
        String resetLink = "http://localhost:3000/forgetpassword/changepassword?token=" + resetToken;

        resetTokenRepository.save(
                PasswordResetToken.builder()
                        .email(email)
                        .token(resetToken)
                        .expiration(LocalDateTime.now().plusMinutes(30))
                        .build()
        );

        if (sendEmail(email, "Réinitialisation de mot de passe",
                "Cliquez ici pour réinitialiser votre mot de passe :\n" + resetLink)) {
            return "📧 Lien de réinitialisation envoyé.";
        } else {
            return "⚠️ Échec de l'envoi de l'email.";
        }
    }

    @Transactional
    public String resetPasswordByToken(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = resetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) return "❌ Token invalide.";

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.getExpiration().isBefore(LocalDateTime.now()))
            return "⏰ Token expiré.";

        Optional<User> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) return "❌ Utilisateur introuvable.";

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetTokenRepository.delete(resetToken);

        logService.log(user, "RESET_PASSWORD");

        return "✅ Mot de passe réinitialisé avec succès.";
    }

    private boolean sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("smartlearn907@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur email : " + e.getMessage());
            return false;
        }
    }

    private String generateOtpCode() {
        return String.valueOf(new Random().nextInt(900_000) + 100_000);
    }

    private record TempUser(
            String firstName,
            String lastName,
            String email,
            String password,
            String company,
            String otpCode,
            LocalDateTime otpGeneratedAt
    ) {}

    public String logout(String token) {
        String email = jwtService.extractUsername(token);
        if (email == null) {
            return "❌ Token invalide.";
        }
    
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "❌ Utilisateur non trouvé.";
        }
    
        User user = userOpt.get();
        user.setLastLoginAt(LocalDateTime.now()); // 🔄 ou tu peux ajouter lastLogoutAt
        userRepository.save(user);
        logService.log(user, "\"👋 Utilisateur déconnecté : {}\"");
        return "✅ Déconnexion réussie.";
    }
    
    
} 
