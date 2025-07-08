package com.example.demo.controller;

import com.example.demo.dto.MessageDto;
import com.example.demo.entity.Message;
import com.example.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    //  Enregistrer un message
    @PostMapping
    public Message saveMessage(@RequestBody MessageDto dto) {
        try {
            System.out.println("📥 Message reçu :");
            System.out.println("ticketId: " + dto.getTicketId());
            System.out.println("content: " + dto.getContent());
            System.out.println("senderType: " + dto.getSenderType());
            System.out.println("senderId: " + dto.getSenderId());
            System.out.println("channel: " + dto.getChannel());
            System.out.println("status: " + dto.getStatus());
            System.out.println("timestamp: " + dto.getTimestamp());

            return messageService.saveMessage(dto);
        } catch (Exception e) {
            System.err.println("❌ Erreur dans MessageController: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    //  Obtenir tous les messages d’un ticket (historique)
    @GetMapping("/ticket/{ticketId}")
    public List<Message> getMessagesByTicket(@PathVariable Long ticketId) {
        return messageService.getMessagesByTicket(ticketId);
    }
}
