package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import ru.kors.chatsservice.models.entity.ChatRole;
import ru.kors.chatsservice.services.ChatRoleService;
import ru.kors.chatsservice.services.ChatService;
import ru.kors.chatsservice.services.UserService;

import java.io.IOException;

@RequiredArgsConstructor
public class ChatRoleDeserializer extends JsonDeserializer<ChatRole> {
    private final UserService userService;
    private final ChatService chatService;

    @Override
    public ChatRole deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        ChatRole chatRole = new ChatRole();

        if (node.has("id")) {
            chatRole.setId(node.get("id").asLong());
        }

        if (node.has("user_id")) {
            chatRole.setUser(userService.findById(node.get("user_id").asLong()));
        }

        if (node.has("chat_id")) {
            chatRole.setChat(chatService.findById(node.get("chat_id").asLong()));
        }

        if (node.has("role")) {
            chatRole.setRole(node.get("role").asText());
        }

        return chatRole;
    }
}
