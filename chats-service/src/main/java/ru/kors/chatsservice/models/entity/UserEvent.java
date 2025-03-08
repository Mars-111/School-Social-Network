package ru.kors.chatsservice.models.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.abstracts.BaseEvent;
import ru.kors.chatsservice.models.entity.deserializers.UserEventDeserializer;
import ru.kors.chatsservice.models.entity.serializers.UserEventSerializer;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("user") // Значение дисриминатора для объектов этого класса
@JsonSerialize(using = UserEventSerializer.class)
@JsonDeserialize(using = UserEventDeserializer.class)
public class UserEvent extends BaseEvent {

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

}

