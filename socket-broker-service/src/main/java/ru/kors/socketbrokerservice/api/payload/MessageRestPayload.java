package ru.kors.socketbrokerservice.api.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private Long messageId;
    private Long chatId;
    private Long senderId;
    private String content;
}