//package ru.kors.socketbrokerservice.models.entity.serializers;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import ru.kors.socketbrokerservice.models.entity.Message;
//import ru.kors.socketbrokerservice.models.entity.Media;
//
//import java.io.IOException;
//
//public class MessageSerializer extends JsonSerializer<Message> {
//    @Override
//    public void serialize(Message message, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//        gen.writeStartObject();
//
//        gen.writeNumberField("id", message.getId());
//        gen.writeStringField("type", message.getType());
//        gen.writeNumberField("flags", message.getFlags());
//        gen.writeNumberField("chat_id", message.getChatId());
//        gen.writeNumberField("sender_id", message.getSenderId());
//        gen.writeObjectField("timestamp", message.getTimestamp());
//
//        if (message.getContent() != null) {
//            gen.writeStringField("content", message.getContent());
//        }
//        if (message.getReplyToId() != null) {
//            gen.writeNumberField("reply_to_id", message.getReplyToId());
//        }
//        if (message.getForwardedFrom() != null) {
//            gen.writeNumberField("forwarded_from", message.getForwardedFrom());
//        }
//
//        if (message.getMediaList() != null && !message.getMediaList().isEmpty()) {
//            gen.writeArrayFieldStart("media");
//            for (Media media : message.getMediaList()) {
//                gen.writeStartObject();
//                gen.writeStringField("media_type", media.getType());
//                gen.writeStringField("media_url", media.getUrl());
//                gen.writeEndObject();
//            }
//            gen.writeEndArray();
//        }
//
//        gen.writeEndObject();
//    }
//}
