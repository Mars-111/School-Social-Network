//package ru.kors.chatsservice.models.entity;
//
//
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import ru.kors.chatsservice.models.entity.deserializers.ChatEventDeserializer;
//import ru.kors.chatsservice.models.entity.serializers.ChatEventSerializer;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//@DiscriminatorValue("message")  // Значение дисриминатора для объектов этого класса
//@JsonSerialize(using = MessageEventSerializer.class)
//@JsonDeserialize(using = MessageEventDeserializer.class)
//public class MessageEvent {
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "chat_id", nullable = false)
//    private Chat chat;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "message_id", nullable = false)
//    private Message message;
//}
