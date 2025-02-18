package ru.kors.socketbrokerservice.models.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.kors.socketbrokerservice.models.ChatRole;

import java.io.IOException;

public class ChatRoleDeserializer extends JsonDeserializer<ChatRole> {

    @Override
    public ChatRole deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);

        Long id = node.has("id") ? node.get("id").asLong() : null;
        Long userId = node.has("user_id") ? node.get("user_id").asLong() : null;
        Long chatId = node.has("chat_id") ? node.get("chat_id").asLong() : null;
        String role = node.has("role") ? node.get("role").asText() : null;

        return new ChatRole(id, userId, chatId, role);
    }
}
