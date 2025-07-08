package com.example.demo.controller;

import com.example.demo.dto.MessageDto;
import com.example.demo.entity.Message;
import com.example.demo.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    // Envoie de message via WebSocket (canal "/app/chat")
    @MessageMapping("/chat")
    public void handleMessage(@Payload MessageDto dto) {
        // Enregistrement en base
        Message saved = messageService.saveMessage(dto);

        // Diffusion aux abonnés du ticket
        String destination = "/topic/messages/" + dto.getTicketId();
        messagingTemplate.convertAndSend(destination, saved);
    }
}
