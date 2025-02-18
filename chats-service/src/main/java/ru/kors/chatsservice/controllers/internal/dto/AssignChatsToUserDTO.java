package ru.kors.chatsservice.controllers.internal.dto;

import java.util.List;

public record AssignChatsToUserDTO(
        Long userId,
        List<Long> chatsIds
    )
{
}
