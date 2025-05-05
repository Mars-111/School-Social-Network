package ru.kors.chatsservice.models.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.deserializers.MessageDeserializer;
import ru.kors.chatsservice.models.entity.serializers.MessageSerializer;


import java.time.Instant;
import java.util.Collections;
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

    private Integer flags = 0; //для разных флагов (например, прочитано/не прочитано, закреп и т.д.)

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @JoinColumn(name = "sender_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User sender;

    private String content;

    @Column(name = "media")
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MessageMedia> mediaList = Collections.emptyList();

    @ManyToOne(fetch = FetchType.EAGER) // связь с родительским сообщением
    @JoinColumn(name = "reply_to_id")
    private Message replyTo; // новое поле для ответа на сообщение

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forwarded_id")
    private Message forwardedFrom;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = Instant.now();
    }
}
