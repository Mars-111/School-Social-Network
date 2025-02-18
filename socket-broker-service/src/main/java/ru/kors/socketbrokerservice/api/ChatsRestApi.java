package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.api.payload.UpdateChatPayload;
import ru.kors.socketbrokerservice.models.Chat;
import ru.kors.socketbrokerservice.models.ChatRole;
import ru.kors.socketbrokerservice.models.JoinRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatsRestApi {

    private final RestClient restClient;
    private final String baseUrl = "http://localhost:8080/internal/api/chats";  // Adjust the URL as necessary

    private static final ParameterizedTypeReference<Set<Chat>> CHAT_LIST_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Set<ChatRole>> CHAT_ROLE_LIST_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<JoinRequest>> JOIN_REQUEST_LIST_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};

    public Set<Chat> findAll() {
        try {
            return restClient.get()
                    .uri(baseUrl)
                    .retrieve()
                    .body(CHAT_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error retrieving all chats", e);
            return Set.of();
        }
    }

    public Optional<Chat> findById(Long id) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/" + id)
                    .retrieve()
                    .body(Chat.class));
        } catch (RestClientException e) {
            log.error("Error retrieving chat by ID: {}", id, e);
            return Optional.empty();
        }
    }

    public Optional<Chat> findByTag(String chatTag) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/tag/" + chatTag)
                    .retrieve()
                    .body(Chat.class));
        } catch (RestClientException e) {
            log.error("Error retrieving chat by tag: {}", chatTag, e);
            return Optional.empty();
        }
    }

    public Set<Chat> findAllByName(String name) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/name/" + name)
                    .retrieve()
                    .body(CHAT_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error retrieving chats by name: {}", name, e);
            return Set.of();
        }
    }

    public Chat createChat(Chat chat) {
        try {
            return restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(chat)
                    .retrieve()
                    .body(Chat.class);
        } catch (RestClientException e) {
            log.error("Error creating chat", e);
            return null;
        }
    }

    public Chat updateChat(Long id, UpdateChatPayload updateChatPayload) {
        try {
            return restClient.put()
                    .uri(baseUrl + "/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateChatPayload)
                    .retrieve()
                    .body(Chat.class);
        } catch (RestClientException e) {
            log.error("Error updating chat with ID: {}", id, e);
            return null;
        }
    }

    public void deleteChat(Long chatId) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + chatId)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error deleting chat with ID: {}", chatId, e);
        }
    }

    public void assignRole(Long chatId, Long userId, String role) {
        try {
            restClient.post()
                    .uri(baseUrl + "/" + chatId + "/roles?userId=" + userId + "&role=" + role)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error assigning role '{}' to user ID {} in chat ID {}", role, userId, chatId, e);
        }
    }

    public Set<ChatRole> getRoles(Long chatId, Long userId) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/" + chatId + "/roles/" + userId)
                    .retrieve()
                    .body(CHAT_ROLE_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error retrieving roles for user ID {} in chat ID {}", userId, chatId, e);
            return Set.of();
        }
    }

    public void unassignRole(Long chatId, Long userId, String role) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + chatId + "/roles/" + userId + "?role=" + role)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error removing role '{}' from user ID {} in chat ID {}", role, userId, chatId, e);
        }
    }

    public List<JoinRequest> findAllJoinRequestsByChat(Long chatId) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/" + chatId + "/join-request")
                    .retrieve()
                    .body(JOIN_REQUEST_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error retrieving join requests for chat ID {}", chatId, e);
            return List.of();
        }
    }

    public JoinRequest createJoinRequest(Long chatId, Long userId) {
        try {
            return restClient.post()
                    .uri(baseUrl + "/" + chatId + "/join-request?userId=" + userId)
                    .retrieve()
                    .body(JoinRequest.class);
        } catch (RestClientException e) {
            log.error("Error creating join request for user ID {} in chat ID {}", userId, chatId, e);
            return null;
        }
    }

    public void deleteJoinRequest(Long requestId) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/join-request/" + requestId)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error deleting join request with ID {}", requestId, e);
        }
    }
}
