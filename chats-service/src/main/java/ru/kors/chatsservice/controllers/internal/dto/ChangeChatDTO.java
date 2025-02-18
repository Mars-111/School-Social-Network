package ru.kors.chatsservice.controllers.internal.dto;


public record ChangeChatDTO(
        String tag,
        String name,
        Boolean privateChat,
        Long ownerId
) {

}
