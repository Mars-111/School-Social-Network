package ru.kors.socketbrokerservice.controllers.payload;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.kors.socketbrokerservice.exceptions.BadPayload;

@Builder
@Getter
public class MessagePayload {
    private Long chatId;
    private final String content;
    private final String type;

    @JsonCreator
    public MessagePayload(@JsonProperty("content") String content,
                          @JsonProperty("type") String type,
                          @JsonProperty("chatId") Long chatId) {
        if (type == null || chatId == null) {
            throw new BadPayload("Chat id and type cannot be null");
        }
        this.content = content;
        this.type = type;
        this.chatId = chatId;
    }
}
