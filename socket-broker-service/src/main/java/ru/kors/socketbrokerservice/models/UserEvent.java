package ru.kors.socketbrokerservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.kors.socketbrokerservice.models.abstracts.BaseEvent;

@Getter
@Setter
public class UserEvent extends BaseEvent {
    @JsonProperty("user_id")
    private Long userId;
}
