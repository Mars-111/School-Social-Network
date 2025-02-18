package ru.kors.chatsservice.models.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.embeddable.ReadMessageId;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "read_messages", indexes = {
        @Index(name = "idx_user_id", columnList = "userId")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ReadMessage {

    @EmbeddedId
    private ReadMessageId id;

    @ManyToOne
    @MapsId("messageId")
    @JoinColumn(name = "message_id", nullable = false)
    //@JsonManagedReference(value = "read-messages")
    private Message message;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = LocalDateTime.now();
    }
}