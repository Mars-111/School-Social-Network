package ru.kors.socketbrokerservice.api.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {
    private Long chatId;
    private String chatName;
}