package ru.kors.socketbrokerservice.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.kors.socketbrokerservice.models.ChatEvent;
import ru.kors.socketbrokerservice.models.Message;
import ru.kors.socketbrokerservice.models.UserEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageKafkaConsumer {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "chat_messages")
    public void consumeMessage(Message message) {
        System.out.println("Получено сообщение: " + message);
        messagingTemplate.convertAndSend("/chat/" + message.getChatId() + "/messages", message);
    }

    @KafkaListener(topics = "chat-events")
    public void consumeChatEvent(ChatEvent event) {
        log.info("Getting new chat event: ", event);

        messagingTemplate.convertAndSend("/chat/" + event.getChatId() + "/events", event);
    }

    @KafkaListener(topics = "user-events")
    public void consumeUserEvent(UserEvent event) {
        log.info("Getting new user event: ", event);

        messagingTemplate.convertAndSend("/user/" + event.getUserId() + "/events", event);
    }

    @KafkaListener(topics = "test")
    public void consumeTest(String message) {
        System.out.println("Получено message: " + message);
    }
}

