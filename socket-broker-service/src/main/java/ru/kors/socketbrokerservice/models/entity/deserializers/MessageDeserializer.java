//package ru.kors.socketbrokerservice.models.entity.deserializers;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonToken;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import ru.kors.socketbrokerservice.models.entity.Message;
//import ru.kors.socketbrokerservice.models.entity.Media;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MessageDeserializer extends JsonDeserializer<Message> {
//
//    @Override
//    public Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        Message message = new Message();
//        List<Media> mediaList = new ArrayList<>();
//
//        while (!p.isClosed() && p.nextToken() != JsonToken.END_OBJECT) {
//            String fieldName = p.getCurrentName();
//            if (fieldName == null) continue;
//
//            p.nextToken(); // move to value
//
//            switch (fieldName) {
//                case "id":
//                    message.setId(p.getLongValue());
//                    break;
//                case "type":
//                    message.setType(p.getText());
//                    break;
//                case "flags":
//                    message.setFlags(p.getIntValue());
//                    break;
//                case "chat_id":
//                    message.setChatId(p.getLongValue());
//                    break;
//                case "sender_id":
//                    message.setSenderId(p.getLongValue());
//                    break;
//                case "content":
//                    message.setContent(p.getText());
//                    break;
//                case "timestamp":
//                    message.setTimestamp(Instant.parse(p.getText()));
//                    break;
//                case "reply_to_id":
//                    message.setReplyToId(p.getLongValue());
//                    break;
//                case "forwarded_from":
//                    message.setForwardedFrom(p.getLongValue());
//                    break;
//                case "media":
//                    if (p.currentToken() == JsonToken.START_ARRAY) {
//                        while (p.nextToken() != JsonToken.END_ARRAY) {
//                            Media media = new Media();
//                            while (p.nextToken() != JsonToken.END_OBJECT) {
//                                String mediaField = p.getCurrentName();
//                                p.nextToken();
//                                if ("media_type".equals(mediaField)) {
//                                    media.setType(p.getText());
//                                } else if ("media_url".equals(mediaField)) {
//                                    media.setUrl(p.getText());
//                                }
//                            }
//                            mediaList.add(media);
//                        }
//                    }
//                    break;
//            }
//        }
//
//        message.setMediaList(mediaList);
//        return message;
//    }
//}
