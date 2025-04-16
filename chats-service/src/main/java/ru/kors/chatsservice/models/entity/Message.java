package ru.kors.chatsservice.models.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.deserializers.ChatDeserializer;
import ru.kors.chatsservice.models.entity.deserializers.MessageDeserializer;
import ru.kors.chatsservice.models.entity.serializers.ChatSerializer;
import ru.kors.chatsservice.models.entity.serializers.MessageSerializer;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "messages", indexes = {
        @Index(name = "idx_messages_chat_id", columnList = "chatId"),
        @Index(name = "idx_messages_user_id", columnList = "sender_id")
})
@JsonSerialize(using = MessageSerializer.class)
@JsonDeserialize(using = MessageDeserializer.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @JoinColumn(name = "sender_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User sender;

    @Column(nullable = false)
    private String content;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReadMessage> readMessages;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = Instant.now();
    }
}
