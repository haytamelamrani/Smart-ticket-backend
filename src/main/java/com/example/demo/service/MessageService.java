package com.example.demo.service;

import com.example.demo.dto.MessageDto;
import com.example.demo.entity.Message;
import java.util.List;

public interface MessageService {
    Message saveMessage(MessageDto dto);
    List<Message> getMessagesByTicket(Long ticketId);
}
