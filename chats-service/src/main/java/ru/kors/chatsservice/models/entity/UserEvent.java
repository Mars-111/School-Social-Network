package ru.kors.chatsservice.models.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.JdbcTypeCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.type.SqlTypes;
import ru.kors.chatsservice.models.entity.deserializers.UserEventDeserializer;
import ru.kors.chatsservice.models.entity.serializers.UserEventSerializer;

import java.time.Instant;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "users_events", indexes = {
        @Index(name = "idx_events_type", columnList = "type"),
        @Index(name = "idx_user_events_user", columnList = "user_id")
})
@JsonSerialize(using = UserEventSerializer.class)
@JsonDeserialize(using = UserEventDeserializer.class)
public class UserEvent {

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

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = Instant.now();
    }
}