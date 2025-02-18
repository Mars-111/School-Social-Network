package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import ru.kors.chatsservice.models.entity.User;

import java.io.IOException;

public class UserDeserializer extends JsonDeserializer<User> {
    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        User user = new User();

        if (node.has("id")) {
            user.setId(node.get("id").asLong());
        }

        if (node.has("tag")) {
            user.setTag(node.get("tag").asText());
        }

        if (node.has("keycloak_id")) {
            user.setKeycloakId(node.get("keycloak_id").asText());
        }

        return user;
    }
}
