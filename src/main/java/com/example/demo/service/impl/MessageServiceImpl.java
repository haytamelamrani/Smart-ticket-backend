package com.example.demo.service.impl;

import com.example.demo.dto.MessageDto;
import com.example.demo.entity.Message;
import com.example.demo.entity.Ticket;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;

    @Override
    public Message saveMessage(MessageDto dto) {
        Ticket ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));

        Message message = Message.builder()
                .ticket(ticket)
                .content(dto.getContent())
                .senderType(dto.getSenderType())
                .senderId(dto.getSenderId())
                .channel(dto.getChannel())
                .timestamp(LocalDateTime.now())
                .status(
                    "agent".equals(dto.getChannel()) ? "sent" : null
                )

                .build();

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesByTicket(Long ticketId) {
        return messageRepository.findByTicketIdOrderByTimestampAsc(ticketId);
    }
}