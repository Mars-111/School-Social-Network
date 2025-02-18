package ru.kors.chatsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.models.entity.*;
import ru.kors.chatsservice.models.entity.abstracts.BaseEvent;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void send(String topic, Long key, BaseEvent event) {
        kafkaTemplate.send(topic, key, event);
    }

    public void send(String topic, BaseEvent event) {
        kafkaTemplate.send(topic, event);
    }

    public void send(Message message) {
        kafkaTemplate.send("chat-messages", message.getId(), message);
    }

    public void send(ChatEvent event) {
        kafkaTemplate.send("chat-events", event.getChat().getId(), event);
    }

    public void send(UserEvent event) {
        kafkaTemplate.send("user-events", event.getUser().getId(), event);
    }

    public void sendPersonal(UserEvent event) {
        kafkaTemplate.send("personal-events", event.getUser().getId(), event);
    }

    public void sendTest(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendCreateChat(Chat chat) {
        kafkaTemplate.send("new-chats", chat);
    }
}
