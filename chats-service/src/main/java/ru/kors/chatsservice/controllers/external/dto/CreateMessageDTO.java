package ru.kors.chatsservice.controllers.external.dto;

public record CreateMessageDTO(
        String type,
        Long chatId,
        String content
        )
{
}
