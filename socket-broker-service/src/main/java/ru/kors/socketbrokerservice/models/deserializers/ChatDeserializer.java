package ru.kors.socketbrokerservice.models.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.kors.socketbrokerservice.models.Chat;

import java.io.IOException;

public class ChatDeserializer extends JsonDeserializer<Chat> {

    @Override
    public Chat deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);

        Long id = node.has("id") ? node.get("id").asLong() : null;
        Long ownerId = node.has("owner_id") ? node.get("owner_id").asLong() : null;
        String tag = node.has("tag") ? node.get("tag").asText() : null;
        String name = node.has("name") ? node.get("name").asText() : null;
        Boolean privateChat = node.has("private") ? node.get("private").asBoolean() : null;

        return new Chat(id, ownerId, tag, name, privateChat);
    }
}

