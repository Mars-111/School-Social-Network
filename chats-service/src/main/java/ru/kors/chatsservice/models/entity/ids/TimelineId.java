package ru.kors.chatsservice.models.entity.ids;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimelineId implements Serializable {
    private Long chatId;
    private Integer timelineId;
}
