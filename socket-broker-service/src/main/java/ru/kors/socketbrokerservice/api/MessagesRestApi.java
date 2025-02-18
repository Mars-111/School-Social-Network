package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.models.Message;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagesRestApi {

    private final RestClient restClient;
    private final String baseUrl = "http://localhost:8082/internal/api/messages";

    private static final ParameterizedTypeReference<List<Message>> MESSAGE_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    public List<Message> findAll() {
        try {
            return restClient.get()
                    .uri(baseUrl)
                    .retrieve()
                    .body(MESSAGE_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error fetching all messages", e);
            return List.of();
        }
    }

    public Optional<Message> findById(Long messageId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/" + messageId)
                    .retrieve()
                    .body(Message.class));
        } catch (RestClientException e) {
            log.error("Error fetching message with ID: {}", messageId, e);
            return Optional.empty();
        }
    }

    public List<Message> findAllByChatId(Long chatId) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/chat/" + chatId)
                    .retrieve()
                    .body(MESSAGE_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error fetching messages for chat ID: {}", chatId, e);
            return List.of();
        }
    }

    public Message createMessage(Message messagePayload) {
        try {
            return  restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(messagePayload)
                    .retrieve()
                    .body(Message.class);
        } catch (RestClientException e) {
            log.error("Error creating message", e);
            return null;
        }
    }

    public void deleteMessage(Long messageId) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + messageId)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error deleting message with ID: {}", messageId, e);
        }
    }
}
