package ru.kors.chatsservice.models.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.kors.chatsservice.models.entity.ids.TimelineId;

@Entity
@Table(name = "timeline")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Timeline {

    @EmbeddedId
    private TimelineId id;

    // Тип: 1 = message, 2 = event
    @Column(name = "type", nullable = false)
    private Integer type;
}


