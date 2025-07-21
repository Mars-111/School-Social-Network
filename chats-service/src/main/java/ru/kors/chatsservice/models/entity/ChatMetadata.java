package ru.kors.chatsservice.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.Instant;

@Entity
@Table(name = "chat_metatada")
@Getter
@Setter
public class ChatMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    private String type;

    private String key;

    private String value;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = Instant.now();
    }
}
