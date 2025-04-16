package ru.kors.socketbrokerservice.models.entity;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class JoinRequest {

    private Long id;

    private Long userId;

    private Long chatId;

    private LocalDateTime timestamp;
}
