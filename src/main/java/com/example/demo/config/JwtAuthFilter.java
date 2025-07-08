package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        System.out.println("🔍 Requête entrante : " + request.getRequestURI());
        System.out.println("🔐 Authorization header : " + authHeader);

        // Vérifie si le header est bien formé
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ Aucun token Bearer trouvé, on passe au filtre suivant.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extrait le JWT
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        System.out.println("📧 Email extrait du token : " + userEmail);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                System.out.println("✅ Utilisateur chargé : " + userDetails.getUsername());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("✅ Token JWT valide pour : " + userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("🔐 Authentification injectée dans le contexte Spring");
                } else {
                    System.out.println("❌ Token JWT invalide");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur lors du chargement de l'utilisateur ou validation du token : " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
