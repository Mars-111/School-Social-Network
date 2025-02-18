package ru.kors.chatsservice.models.entity.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.kors.chatsservice.models.entity.Message;

import java.io.IOException;

public class MessageSerializer extends JsonSerializer<Message> {
    @Override
    public void serialize(Message message, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", message.getId());
        gen.writeNumberField("chat_id", message.getChat().getId());
        gen.writeNumberField("sender_id", message.getSender().getId());
        gen.writeStringField("type", message.getType());
        gen.writeStringField("content", message.getContent());
        gen.writeObjectField("timestamp", message.getTimestamp());
        gen.writeEndObject();
    }
}