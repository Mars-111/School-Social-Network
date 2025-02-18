package ru.kors.chatsservice.models.entity.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.kors.chatsservice.models.entity.UserEvent;

import java.io.IOException;

public class UserEventSerializer extends JsonSerializer<UserEvent> {
    @Override
    public void serialize(UserEvent userEvent, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (userEvent.getId() != null) {
            jsonGenerator.writeNumberField("id", userEvent.getId());
        }
        jsonGenerator.writeStringField("type", userEvent.getType());
        jsonGenerator.writeNumberField("user_id", userEvent.getUser().getId());

        if (userEvent.getData() != null) {
            jsonGenerator.writeFieldName("data");
            jsonGenerator.writeTree(userEvent.getData());
        }

        if (userEvent.getTimestamp() != null) {
            jsonGenerator.writeStringField("timestamp", userEvent.getTimestamp().toString());
        }

        jsonGenerator.writeEndObject();
    }
}
