package ru.kors.socketbrokerservice.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserEvent {
    private Long id;

    private String type;

    @JsonProperty("user_id")
    private Long userId;

    private JsonNode data;

    private Instant timestamp;
}
