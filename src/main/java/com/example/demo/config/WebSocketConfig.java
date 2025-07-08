package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //  Active le broker interne pour les messages sortants
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // les clients s'abonnent ici
        config.setApplicationDestinationPrefixes("/app"); // les clients envoient ici
    }

    //  Point de connexion WebSocket
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // autorise tous les domaines (à adapter en prod)
                .withSockJS(); // fallback si WebSocket non supporté
    }
}
