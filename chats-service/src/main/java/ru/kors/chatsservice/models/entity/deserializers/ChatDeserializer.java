package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.repositories.UserRepository;
import ru.kors.chatsservice.services.UserService;

import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
public class ChatDeserializer extends JsonDeserializer<Chat> {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Chat deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Chat chat = new Chat();

        if (node.has("id")) {
            chat.setId(node.get("id").asLong());
        }

        if (node.has("tag")) {
            chat.setTag(node.get("tag").asText());
        }

        if (node.has("name")) {
            chat.setName(node.get("name").asText());
        }

        if (node.has("type")) {
            chat.setType(node.get("type").asText());
        }

        if (node.has("owner_id")) {
            chat.setOwner(userRepository.getReferenceById(node.get("owner_id").asLong()));
        }

        if (node.has("created_at")) {
            chat.setCreatedAt(Instant.ofEpochMilli(node.get("created_at").asLong()));
        }

        return chat;
    }
}
