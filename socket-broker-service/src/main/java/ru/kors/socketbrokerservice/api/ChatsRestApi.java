package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.api.payload.Chat;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ChatsRestChatsApi {
    private final RestClient restClient;
    private final String baseUrl = "http://localhost:8080/api/chats";
    private static final ParameterizedTypeReference<List<Chat>> CHAT_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {};

    public List<Chat> getAllChats() {
        return restClient.get()
                .uri(baseUrl)
                .retrieve()
                .body(CHAT_LIST_TYPE_REFERENCE);
    }

    public Optional<Chat> getChatById(Long chatId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/" + chatId)
                    .retrieve()
                    .body(Chat.class));
        } catch (RestClientException e) {
            // Handle exception
            return Optional.empty();
        }
    }

    public Optional<Chat> getChatByName(String chatName) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/byName/" + chatName)
                    .retrieve()
                    .body(Chat.class));
        } catch (RestClientException e) {
            // Handle exception
            return Optional.empty();
        }
    }

    public Chat createChat(String chatName) {
        try {
            return restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(chatName)
                    .retrieve()
                    .body(Chat.class);
        } catch (RestClientException e) {
            // Handle exception
            return null;
        }
    }

    public void deleteChat(Long chatId) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + chatId)
                    .retrieve();
        } catch (RestClientException e) {
            // Handle exception
        }
    }
}
