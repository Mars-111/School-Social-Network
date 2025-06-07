//package ru.kors.chatsservice.models.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.Instant;
//
//
////Я решил убрать связи чтобы оптимизировать запросы и уменьшить количество join-ов.
////Как по мне тут связи ни к чему
//@Entity
//@Getter
//@Setter
//@Table(name = "last_message_read_in_chat",
//        indexes = {
//                @Index(name = "idx_user_chat", columnList = "user_id, chat_id")
//        }
//)
//public class LastMessageReadInChat {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "user_id")
//    private Long userId;
//
//    @Column(name = "chat_id")
//    private Long chatId;
//
//    @Column(name = "message_id")
//    private Long messageId; // Связываем с сообщением
//
//    @Column(name = "timestamp", nullable = false, updatable = false)
//    private Instant timestamp;
//
//    @PrePersist
//    private void setTimestamp() {
//        this.timestamp = Instant.now();
//    }
//}
