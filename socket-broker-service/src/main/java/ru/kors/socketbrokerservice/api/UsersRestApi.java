package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.api.payload.CreateUserPayload;
import ru.kors.socketbrokerservice.api.payload.UserRestPayload;
import ru.kors.socketbrokerservice.models.Chat;
import ru.kors.socketbrokerservice.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersRestApi {

    private final RestClient restClient;
    private final String baseUrl = "http://localhost:8082/internal/api/users";

    private static final ParameterizedTypeReference<Set<User>> USER_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<Set<Chat>> CHAT_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    public Set<User> findAll() {
        return restClient.get()
                .uri(baseUrl)
                .retrieve()
                .body(USER_LIST_TYPE_REFERENCE);
    }

    public Optional<User> findById(Long id) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/" + id)
                    .retrieve()
                    .body(User.class));
        } catch (RestClientException e) {
            log.error("Error fetching user by ID: {}", id, e);
            return Optional.empty();
        }
    }

    public Optional<User> findByKeycloakId(String keycloakId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/keycloak/" + keycloakId)
                    .retrieve()
                    .body(User.class));
        } catch (RestClientException e) {
            log.error("Error fetching user by Keycloak ID: {}", keycloakId, e);
            return Optional.empty();
        }
    }

    public Set<Chat> getUserChats(Long userId) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/" + userId + "/chats")
                    .retrieve()
                    .body(CHAT_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error fetching chats for user ID: {}", userId, e);
            return Set.of();
        }
    }

    public User createUser(CreateUserPayload user) {
        try {
            return restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(user)
                    .retrieve()
                    .body(User.class);
        } catch (RestClientException e) {
            log.error("Error while creating user: ", e);
            return null;
        }
    }

    public void deleteById(Long id) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + id)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error deleting user by ID: {}", id, e);
        }
    }

    public void assignChatsToUser(Long userId, Set<Long> chatsIds) {
        try {
            restClient.post()
                    .uri(baseUrl + "/" + userId + "/chats/assign")
                    .body(chatsIds)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error assign sets chat to user {}", userId);
        }
    }

    public Boolean isUserInChat(Long userId, Long chatId) { //ОБЯЗАТЕЛЬНО ДОЛЖЕН РАБОТАТЬ
        try {
            return restClient.post()
                    .uri(baseUrl + "/" + userId + "/chats/" + chatId + "/have")
                    .retrieve()
                    .body(Boolean.class);
        } catch (RestClientException e) {
            log.error("Error assign chat {} to user {}",  chatId, userId);
            return false;
        }
    }
}
