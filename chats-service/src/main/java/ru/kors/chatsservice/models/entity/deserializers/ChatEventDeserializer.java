package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.ChatEvent;
import ru.kors.chatsservice.services.ChatService;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ChatEventDeserializer extends JsonDeserializer<ChatEvent> {

    private final ChatService chatService;

    @Override
    public ChatEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode root = mapper.readTree(jsonParser);

        ChatEvent chatEvent = new ChatEvent();

        if (root.has("id")) {
            chatEvent.setId(root.get("id").asLong());
        }
        if (root.has("type")) {
            chatEvent.setType(root.get("type").asText());
        }
        if (root.has("user_id")) {
            Chat chat = chatService.findById(root.get("user_id").asLong());
            chatEvent.setChat(chat);
        }
        if (root.has("data")) {
            chatEvent.setData(root.get("data"));
        }
        if (root.has("timestamp")) {
            chatEvent.setTimestamp(Instant.parse(root.get("timestamp").asText()));
        }

        return chatEvent;
    }
}
