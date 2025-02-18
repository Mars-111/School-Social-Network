package ru.kors.chatsservice.models.entity.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ReadMessageId implements Serializable {

    private String userId;
    private Long messageId;

    // Getters, Setters, hashCode and equals
}