package ru.kors.chatsservice.models.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.abstracts.BaseEvent;
import ru.kors.chatsservice.models.entity.deserializers.ChatEventDeserializer;
import ru.kors.chatsservice.models.entity.serializers.ChatEventSerializer;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("chat")  // Значение дисриминатора для объектов этого класса
@Table(name = "events", indexes = {
        @Index(name = "idx_chat_events_chat", columnList = "chat_id")
})
@JsonSerialize(using = ChatEventSerializer.class)
@JsonDeserialize(using = ChatEventDeserializer.class)
public class ChatEvent extends BaseEvent {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;
}


