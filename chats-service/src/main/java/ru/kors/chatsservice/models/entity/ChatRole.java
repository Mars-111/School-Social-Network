package ru.kors.chatsservice.models.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.deserializers.ChatDeserializer;
import ru.kors.chatsservice.models.entity.deserializers.ChatRoleDeserializer;
import ru.kors.chatsservice.models.entity.serializers.ChatRoleSerializer;
import ru.kors.chatsservice.models.entity.serializers.ChatSerializer;

@Entity
@Getter
@Setter
@Table(name = "chats_roles", indexes = {
        @Index(name = "idx_user_chat", columnList = "user_id, chat_id"),
        @Index(name = "idx_user_chat_role", columnList = "user_id, chat_id, role")
})
@JsonSerialize(using = ChatRoleSerializer.class)
@JsonDeserialize(using = ChatRoleDeserializer.class)
@AllArgsConstructor
@NoArgsConstructor
public class ChatRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private String role;
}
