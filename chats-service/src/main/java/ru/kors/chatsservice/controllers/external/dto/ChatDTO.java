package ru.kors.chatsservice.controllers.external.dto;


public record ChatDTO(
        Long id,

        String tag,

        String name,

        Boolean privateChat,

        Long ownerId
) {
    public ChatDTO {
        //Хз надо ли id проверять
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag must be not null and not empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must be not null and not empty");
        }
        if (privateChat == null) {
            throw new IllegalArgumentException("Private chat must be not null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner id must be not null");
        }
    }
}