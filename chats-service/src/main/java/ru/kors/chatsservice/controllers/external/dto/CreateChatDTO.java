package ru.kors.chatsservice.controllers.external.dto;

import ru.kors.chatsservice.models.entity.User;

public record CreateChatDTO (
    String tag,
    String type,
    String name) {
}
