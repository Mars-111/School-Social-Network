package ru.kors.socketbrokerservice.controllers.payload;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.kors.socketbrokerservice.exceptions.UserNotAuthorizedException;
import ru.kors.socketbrokerservice.models.enums.MessageType;

@Builder
@Getter
public class ChatMessagePayload {
    private final String content;
    private final MessageType type;
    @Setter
    private String sender;

    @JsonCreator
    public ChatMessagePayload(@JsonProperty("content") String content,
                              @JsonProperty("type") MessageType type,
                              @JsonProperty("sender") String sender) {
        if (content == null || type == null) {
            throw new UserNotAuthorizedException("Content and type cannot be null");
        }
        this.content = content;
        this.type = type;
        this.sender = sender;
    }
}
