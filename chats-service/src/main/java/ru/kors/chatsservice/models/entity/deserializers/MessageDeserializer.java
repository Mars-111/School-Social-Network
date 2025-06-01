package ru.kors.chatsservice.models.entity.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import ru.kors.chatsservice.models.entity.MediaMetadata;
import ru.kors.chatsservice.models.entity.Message;
    import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.models.entity.Chat;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageDeserializer extends JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);

        Message message = new Message();

        message.setId(getLong(root, "id"));
        message.setType(getString(root, "type"));
        message.setFlags(getInt(root, "flags"));

        Long chatId = getLong(root, "chat_id");
        if (chatId != null) {
            Chat chat = new Chat();
            chat.setId(chatId);
            message.setChat(chat);
        }

        Long senderId = getLong(root, "sender_id");
        if (senderId != null) {
            User sender = new User();
            sender.setId(senderId);
            message.setSender(sender);
        }

        message.setTimestamp(getInstant(root, "timestamp"));
        message.setContent(getString(root, "content"));

        // Вложенный reply_to
        JsonNode replyToNode = root.get("reply_to");
        if (replyToNode != null) {
            Message replyTo = mapper.treeToValue(replyToNode, Message.class);
            message.setReplyTo(replyTo);
        }
        else {
            Long replyToId = getLong(root, "reply_to_id");
            if (replyToId != null) {
                Message replyTo = createRefMessage(replyToId);
                message.setReplyTo(replyTo);
            }
        }

        // Вложенный forwarded_from
        JsonNode forwardedNode = root.get("forwarded_from");
        if (forwardedNode != null) {
            Message forwarded = mapper.treeToValue(forwardedNode, Message.class);
            message.setForwardedFrom(forwarded);
        }
        else {
            Long forwardedFromId = getLong(root, "forwarded_from_id");
            if (forwardedFromId != null) {
                Message forwardedFrom = createRefMessage(forwardedFromId);
                message.setForwardedFrom(forwardedFrom);
            }
        }

//        // media
        JsonNode mediaNode = root.get("media");
        if (mediaNode != null && mediaNode.isArray()) {
            List<MediaMetadata> mediaList = new ArrayList<>();
            for (JsonNode mediaItem : mediaNode) {
                mediaList.add(mediaItem.traverse(mapper).readValueAs(MediaMetadata.class));
            }
            message.setMediaList(mediaList);
        }

        return message;
    }

    private String getString(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : null;
    }

    private Long getLong(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asLong() : null;
    }

    private Integer getInt(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asInt() : 0;
    }

    private Instant getInstant(JsonNode node, String field) {
        return node.has(field) ? Instant.parse(node.get(field).asText()) : null;
    }

    private Message createRefMessage(Long id) {
        Message ref = new Message();
        ref.setId(id);
        return ref;
    }
}
