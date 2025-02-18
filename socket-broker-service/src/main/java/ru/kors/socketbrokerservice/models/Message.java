package ru.kors.socketbrokerservice.models;

import lombok.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ru.kors.socketbrokerservice.models.deserializers.MessageDeserializer;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = MessageDeserializer.class)
public class Message {
    private Long id;

    private String type;

    private Long chatId;

    private Long senderId;

    private String content;

    private LocalDateTime timestamp;
}