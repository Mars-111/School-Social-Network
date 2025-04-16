package ru.kors.socketbrokerservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.kors.socketbrokerservice.models.entity.User;

import java.io.IOException;

public class UserDeserializer extends JsonDeserializer<User> {

    @Override
    public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectNode node = p.getCodec().readTree(p);

        Long id = node.has("id") ? node.get("id").asLong() : null;
        String tag = node.has("tag") ? node.get("tag").asText() : null;
        String keycloakId = node.has("keycloak_id") ? node.get("keycloak_id").asText() : null;

        return new User(id, tag, keycloakId);
    }
}
