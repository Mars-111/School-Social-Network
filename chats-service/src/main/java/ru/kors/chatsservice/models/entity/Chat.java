package ru.kors.chatsservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<Message> messages;

    @ManyToMany(mappedBy = "chats", fetch = FetchType.LAZY)
    private List<User> chatUsers;

}
