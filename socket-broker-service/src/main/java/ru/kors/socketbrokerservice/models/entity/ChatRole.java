package ru.kors.socketbrokerservice.models.entity;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Data
public class ChatRole {

    private Long id;

    private Long userId;

    private Long chatId;

    private String role;
}
