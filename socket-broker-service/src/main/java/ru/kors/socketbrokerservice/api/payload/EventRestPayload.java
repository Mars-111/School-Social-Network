package ru.kors.socketbrokerservice.api.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRestPayload {
    private Long id;
    private Long chatId;
    private String userId;
    private String eventType;
    private Long messageId;
    private String eventData;
}