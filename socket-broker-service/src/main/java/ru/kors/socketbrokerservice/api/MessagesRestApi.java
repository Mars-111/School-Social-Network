package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.api.payload.Message;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MessageRestApi {
    private final RestClient restClient;
    private final String baseUrl = "http://localhost:8080/api/messages";
    private static final ParameterizedTypeReference<List<Message>> MESSAGE_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {};

    public List<Message> getAllMessages() {
        return restClient.get()
                .uri(baseUrl)
                .retrieve()
                .body(MESSAGE_LIST_TYPE_REFERENCE);
    }

    public Optional<Message> getMessageById(Long messageId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/" + messageId)
                    .retrieve()
                    .body(Message.class));
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }

    public List<Message> getMessagesByChatId(Long chatId) {
        return restClient.get()
                .uri(baseUrl + "/chat/" + chatId)
                .retrieve()
                .body(MESSAGE_LIST_TYPE_REFERENCE);
    }

    public Message createMessage(Message message) {
        try {
            return restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(message)
                    .retrieve()
                    .body(Message.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public void deleteMessage(Long messageId) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + messageId)
                    .retrieve();
        } catch (RestClientException e) {
            // Handle exception
        }
    }
}