//package ru.kors.socketbrokerservice.api.payload;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import ru.kors.socketbrokerservice.controllers.payload.SendMessagePayload;
//
//@Getter
//@Setter
//@NoArgsConstructor
//public class MessageRestPayload {
//    private Long chatId;
//    private Long senderId;
//    private String content;
//    private String type;
//
//    public MessageRestPayload(SendMessagePayload messagePayload, Long senderId) {
//        this.chatId = messagePayload.chatId();
//        this.content = messagePayload.content();
//        this.type = messagePayload.type();
//        this.senderId = senderId;
//    }
//}