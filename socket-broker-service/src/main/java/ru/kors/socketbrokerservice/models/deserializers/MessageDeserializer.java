package ru.kors.socketbrokerservice.models.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.kors.socketbrokerservice.models.Message;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageDeserializer extends JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);

        Long id = node.has("id") ? node.get("id").asLong() : null;
        String type = node.has("type") ? node.get("type").asText() : null;
        Long chatId = node.has("chat_id") ? node.get("chat_id").asLong() : null;
        Long senderId = node.has("sender_id") ? node.get("sender_id").asLong() : null;
        String content = node.has("content") ? node.get("content").asText() : null;
        String timestampStr = node.has("timestamp") ? node.get("timestamp").asText() : null;

        LocalDateTime timestamp = null;
        if (timestampStr != null) {
            // Assuming the timestamp is in ISO format, modify this if needed
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            timestamp = LocalDateTime.parse(timestampStr, formatter);
        }

        return new Message(id, type, chatId, senderId, content, timestamp);
    }
}

