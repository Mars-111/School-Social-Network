package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import ru.kors.chatsservice.models.entity.JoinRequest;
import ru.kors.chatsservice.services.ChatService;
import ru.kors.chatsservice.services.UserService;

import java.io.IOException;

@RequiredArgsConstructor
public class JoinRequestDeserializer extends JsonDeserializer<JoinRequest> {
    private final UserService userService;
    private final ChatService chatService;

    @Override
    public JoinRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JoinRequest joinRequest = new JoinRequest();

        if (node.has("id")) {
            joinRequest.setId(node.get("id").asLong());
        }

        if (node.has("user_id")) {
            joinRequest.setUser(userService.findById(node.get("user_id").asLong()));
        }

        if (node.has("chat_id")) {
            joinRequest.setChat(chatService.findById(node.get("chat_id").asLong()));
        }

        return joinRequest;
    }
}
