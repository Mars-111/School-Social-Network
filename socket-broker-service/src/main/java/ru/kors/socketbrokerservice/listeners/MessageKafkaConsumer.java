package ru.kors.socketbrokerservice.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.kors.socketbrokerservice.models.entity.ChatEvent;
import ru.kors.socketbrokerservice.models.entity.Message;
import ru.kors.socketbrokerservice.models.entity.UserEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageKafkaConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "chat-messages")
    public void consumeMessage(@Payload String messageJson) throws JsonProcessingException {
        // Преобразование JSON в объект Message
        Message message = objectMapper.readValue(messageJson, Message.class);
        // Логирование полученного сообщения (для отладки)
        System.out.println("Получено сообщение: " + messageJson);

        // Отправка сообщения в нужный чат по WebSocket
        // Здесь предполагается, что chatId это идентификатор чата
        String destination = "/chat/" + message.getChatId(); // Можно добавить "/messages" или что-то другое в зависимости от вашей логики
        System.out.println("Отправка сообщения {" + message.getId() + "} по пути: " + destination);
        messagingTemplate.convertAndSend(destination, message);
    }

    @KafkaListener(topics = "chat-events")
    public void consumeChatEvent(ChatEvent event) {
        log.info("Getting new chat event: ", event);

        messagingTemplate.convertAndSend("/chat/" + event.getChatId(), event); // + "/events"
    }

    @KafkaListener(topics = "user-events")
    public void consumeUserEvent(UserEvent event) {
        log.info("Getting new user event: ", event);

        messagingTemplate.convertAndSend("/user/" + event.getUserId(), event);
    }

    @KafkaListener(topics = "personal-events")
    public void consumePersonalEvent(UserEvent event) {
        log.info("Getting new personal event: ", event);

        messagingTemplate.convertAndSend("/personal/" + event.getUserId(), event);
    }

    @KafkaListener(topics = "test")
    public void consumeTest(String message) {
        System.out.println("Получено message: " + message);
    }
}

