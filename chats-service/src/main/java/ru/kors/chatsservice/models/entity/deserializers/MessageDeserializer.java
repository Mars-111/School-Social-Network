package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.Message;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.services.ChatService;
import ru.kors.chatsservice.services.UserService;

import java.io.IOException;

@RequiredArgsConstructor
public class MessageDeserializer extends JsonDeserializer<Message> {

    private final ChatService chatService;
    private final UserService userService;

    @Override
    public Message deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Message message = new Message();

        if (node.has("id")) {
            message.setId(node.get("id").asLong());
        }

        if (node.has("type")) {
            message.setType(node.get("type").asText());
        }

        if (node.has("content")) {
            message.setContent(node.get("content").asText());
        }

        if (node.has("chat_id")) {
            Chat chat = chatService.findById(node.get("chat_id").asLong());
            message.setChat(chat);
        }

        if (node.has("sender_id")) {
            User user = userService.findById(node.get("sender_id").asLong());
            message.setSender(user);
        }

        return message;
    }
}
