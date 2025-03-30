package ru.kors.chatsservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.models.entity.*;
import ru.kors.chatsservice.models.entity.abstracts.BaseEvent;
import ru.kors.chatsservice.models.entity.serializers.MessageSerializer;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

//    public void send(String topic, Long key, BaseEvent event) {
//        kafkaTemplate.send(topic, key, event);
//    }

//    public void send(String topic, BaseEvent event) {
//        kafkaTemplate.send(topic, event);
//    }

public void send(Message message) {
    try {
        String jsonMessage = objectMapper.writeValueAsString(message);
        kafkaTemplate.send("chat-messages", message.getChat().getId(), jsonMessage);
        log.info("Message sent to chat-messages: " + jsonMessage);
    } catch (Exception e) {
        log.error("Ошибка при отправке сообщения в Kafka", e);
    }
}

    public void send(ChatEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("chat-events", event.getChat().getId(), jsonEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(UserEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("user-events", event.getUser().getId(), jsonEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPersonal(UserEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("personal-events", event.getUser().getId(), jsonEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTest(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendCreateChat(Chat chat) {
        try {
            String jsonChat = objectMapper.writeValueAsString(chat);
            kafkaTemplate.send("new-chats", jsonChat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
