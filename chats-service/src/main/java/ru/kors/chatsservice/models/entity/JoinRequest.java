package ru.kors.chatsservice.models.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.deserializers.ChatDeserializer;
import ru.kors.chatsservice.models.entity.deserializers.JoinRequestDeserializer;
import ru.kors.chatsservice.models.entity.serializers.ChatSerializer;
import ru.kors.chatsservice.models.entity.serializers.JoinRequestSerializer;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "join_requests")
@JsonSerialize(using = JoinRequestSerializer.class)
@JsonDeserialize(using = JoinRequestDeserializer.class)
public class JoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = LocalDateTime.now();
    }
}