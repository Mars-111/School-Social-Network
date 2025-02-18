//package ru.kors.socketbrokerservice.models.deserializers;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import ru.kors.socketbrokerservice.models.Event;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//public class EventDeserializer extends JsonDeserializer<Event> {
//
//    @Override
//    public Event deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        ObjectNode node = p.getCodec().readTree(p);
//
//        Long id = node.has("id") ? node.get("id").asLong() : null;
//        String type = node.has("type") ? node.get("type").asText() : null;
//        Long chatId = node.has("chat_id") ? node.get("chat_id").asLong() : null;
//        Long messageId = node.has("message_id") ? node.get("message_id").asLong() : null;
//        Long userId = node.has("user_id") ? node.get("user_id").asLong() : null;
//        String data = node.has("data") ? node.get("data").asText() : null;
//        String timestampStr = node.has("timestamp") ? node.get("timestamp").asText() : null;
//
//        LocalDateTime timestamp = null;
//        if (timestampStr != null) {
//            // Assuming the timestamp is in ISO format, you can modify this if needed
//            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//            timestamp = LocalDateTime.parse(timestampStr, formatter);
//        }
//
//        return new Event(id, type, chatId, messageId, userId, data, timestamp);
//    }
//}