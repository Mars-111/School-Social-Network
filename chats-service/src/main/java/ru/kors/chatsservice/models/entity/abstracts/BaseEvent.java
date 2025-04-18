package ru.kors.chatsservice.models.entity.abstracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_category") // Это и есть дисриминатор, который будет определять тип наследника
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "events", indexes = {
        @Index(name = "idx_events_timestamp", columnList = "timestamp"),
        @Index(name = "idx_events_event_type", columnList = "type"),
        @Index(name = "idx_user_events_user", columnList = "user_id"),
        @Index(name = "idx_chat_events_chat", columnList = "chat_id")
})
public abstract class BaseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Можно оставить поле type для дополнительной информации или убрать,
    // если дисриминатор полностью покрывает назначение.
    @Column(name = "type", nullable = false)
    private String type;

    //@Convert(converter = JsonNodeConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonNode data;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = Instant.now();
    }
}
