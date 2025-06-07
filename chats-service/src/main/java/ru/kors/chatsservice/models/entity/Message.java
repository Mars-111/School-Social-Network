package ru.kors.chatsservice.models.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.kors.chatsservice.models.entity.deserializers.MessageDeserializer;
import ru.kors.chatsservice.models.entity.serializers.MessageSerializer;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "messages", indexes = {
        @Index(name = "idx_messages_chat_id", columnList = "chatId"),
        @Index(name = "idx_messages_user_id", columnList = "sender_id")
})
@JsonSerialize(using = MessageSerializer.class)
@JsonDeserialize(using = MessageDeserializer.class)
@Slf4j
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timeline_id", nullable = false)
    private Integer timelineId; //порядковый номер сообщения в чате, для сортировки сообщений в чате

    private Integer flags = 0; //для разных флагов (например, прочитано/не прочитано, закреп и т.д.)

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @JoinColumn(name = "sender_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User sender;

    private String content;

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileMetadata> fileList =  new ArrayList<>(); //List что бы сохранять порядок медиафайлов

    @ManyToOne(fetch = FetchType.EAGER) // связь с родительским сообщением
    @JoinColumn(name = "reply_to_id")
    private Message replyTo; // новое поле для ответа на сообщение

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forwarded_id")
    private Message forwardedFrom;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = Instant.now();
    }


    public void addFile(FileMetadata file) {
        fileList.add(file);
        file.setMessage(this);
    }

}
