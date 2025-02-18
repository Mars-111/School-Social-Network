package ru.kors.chatsservice.models.entity.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.kors.chatsservice.models.entity.Event;

import java.io.IOException;

public class EventSerializer extends JsonSerializer<Event> {

    @Override
    public void serialize(Event event, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        if (event.getId() != null) {
            gen.writeNumberField("id", event.getId());
        }
        gen.writeStringField("event_type", event.getType());
        gen.writeNumberField("chat_id", event.getChatId());
        gen.writeNumberField("user_id", event.getUserId());

        if (event.getData() != null) {
            // Пишем поле data как JSON-объект
            gen.writeFieldName("data");
            gen.writeTree(event.getData());
        }

        if (event.getTimestamp() != null) {
            gen.writeStringField("timestamp", event.getTimestamp().toString());
        }
        gen.writeEndObject();
    }
}
