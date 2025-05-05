package ru.kors.socketbrokerservice.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class Chat {
    private Long id;
    @JsonProperty("owner_id")
    private Long ownerId;
    private String tag;
    private String name;
    @JsonProperty("created_at")
    private Instant createdAt;
}
