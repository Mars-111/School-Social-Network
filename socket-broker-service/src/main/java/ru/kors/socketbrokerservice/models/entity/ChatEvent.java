package ru.kors.socketbrokerservice.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ChatEvent {
    private Long id;

    @JsonProperty("timeline_id")
    private Integer timelineId;

    private String type;

    @JsonProperty("chat_id")
    private Long chatId;

    private JsonNode data;

    private Instant timestamp;
}
