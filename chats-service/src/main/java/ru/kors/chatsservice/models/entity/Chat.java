package ru.kors.chatsservice.models.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kors.chatsservice.models.entity.deserializers.ChatDeserializer;
import ru.kors.chatsservice.models.entity.serializers.ChatSerializer;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "chats")
@JsonSerialize(using = ChatSerializer.class)
@JsonDeserialize(using = ChatDeserializer.class)
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tag;

    @Column(nullable = false)
    private String name;

    //TODO: Добавить тип чата (групповой, приватный, личка и т.д.)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    //https://chatgpt.com/share/677e9b38-89fc-8001-9dd9-6149fc4db443
    private Boolean privateChat = false;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("timestamp ASC") // Сохраняем порядок по времени
    private List<Message> messages;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("timestamp ASC") // Сохраняем порядок по времени
    private List<ChatEvent> events;

    @ManyToMany(mappedBy = "chats", fetch = FetchType.LAZY)
    private Set<User> users;

    //Запросы на вступление в чат. Он должен быть null если privateChat = false.
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("timestamp ASC") // Сохраняем порядок по времени
    private List<JoinRequest> joinRequests;

    //Пользователи с ролями в чате
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ChatRole> chatRoles;

    public Chat(String tag, String name, Boolean privateChat, User owner) {
        this.tag = tag;
        this.name = name;
        this.privateChat = privateChat;
        this.owner = owner;
    }
}
