package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.models.entity.UserEvent;
import ru.kors.chatsservice.services.UserService;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserEventDeserializer extends JsonDeserializer<UserEvent> {
    private final UserService userService;

    @Override
    public UserEvent deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode root = mapper.readTree(jsonParser);

        UserEvent userEvent = new UserEvent();

        if (root.has("id")) {
            userEvent.setId(root.get("id").asLong());
        }
        if (root.has("type")) {
            userEvent.setType(root.get("type").asText());
        }
        if (root.has("user_id")) {
            User user = userService.findById(root.get("user_id").asLong());
            userEvent.setUser(user);
        }
        if (root.has("data")) {
            userEvent.setData(root.get("data"));
        }
        if (root.has("timestamp")) {
            userEvent.setTimestamp(Instant.parse(root.get("timestamp").asText()));
        }

        return userEvent;
    }
}
