package com.example.demo.mapper;

import com.example.demo.dto.TicketRequestDto;
import com.example.demo.dto.TicketResponseDto;
import com.example.demo.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "priority", source = "priority")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "userEmail", source = "userEmail")
    @Mapping(target = "etat", ignore = true) // ou source="etat" si tu veux le prendre depuis le frontend
    Ticket toEntity(TicketRequestDto dto);

    @Mapping(target = "ticketId", expression = "java(\"TK-\" + ticket.getId())")
    @Mapping(target = "message", constant = "Votre ticket a été enregistré.")
    TicketResponseDto toDto(Ticket ticket);
}

