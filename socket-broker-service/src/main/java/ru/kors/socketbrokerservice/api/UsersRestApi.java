package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.models.entity.Chat;
import ru.kors.socketbrokerservice.models.entity.User;

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
    private static final ParameterizedTypeReference<Set<Long>> CHATS_IDS_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

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

    public Set<Long> getUserChatsIds(Long userId) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/" + userId + "/chatsIds")
                    .retrieve()
                    .body(CHATS_IDS_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error fetching chats for user ID: {}", userId, e);
            return Set.of();
        }
    }
}