package ru.kors.socketbrokerservice.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.kors.socketbrokerservice.models.entity.abstracts.BaseEvent;

@Getter
@Setter
public class UserEvent extends BaseEvent {
    @JsonProperty("user_id")
    private Long userId;
}
