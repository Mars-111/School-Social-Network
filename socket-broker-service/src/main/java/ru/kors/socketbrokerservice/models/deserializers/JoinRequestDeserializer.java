package ru.kors.socketbrokerservice.models.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.kors.socketbrokerservice.models.JoinRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JoinRequestDeserializer extends JsonDeserializer<JoinRequest> {

    @Override
    public JoinRequest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);

        Long id = node.has("id") ? node.get("id").asLong() : null;
        Long userId = node.has("user_id") ? node.get("user_id").asLong() : null;
        Long chatId = node.has("chat_id") ? node.get("chat_id").asLong() : null;
        String timestampStr = node.has("timestamp") ? node.get("timestamp").asText() : null;

        LocalDateTime timestamp = null;
        if (timestampStr != null) {
            // Assuming the timestamp is in ISO format, you can modify this if needed
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            timestamp = LocalDateTime.parse(timestampStr, formatter);
        }

        return new JoinRequest(id, userId, chatId, timestamp);
    }
}
