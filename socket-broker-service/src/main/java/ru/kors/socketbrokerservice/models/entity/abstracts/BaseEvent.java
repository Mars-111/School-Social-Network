package ru.kors.socketbrokerservice.models.entity.abstracts;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseEvent {

    private Long id;

    private String type;

    private JsonNode data;

    private Instant timestamp;
}
