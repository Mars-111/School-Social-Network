package ru.kors.socketbrokerservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kors.socketbrokerservice.api.UsersRestApi;
import ru.kors.socketbrokerservice.api.payload.CreateUserPayload;
import ru.kors.socketbrokerservice.models.Chat;
import ru.kors.socketbrokerservice.models.User;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRestApi usersRestApi;

    public Set<User> findAll() {
        return usersRestApi.findAll();
    }

    public User findById(Long id) {
        return usersRestApi.findById(id).orElse(null);
    }

    public User findByKeycloak(String keycloakId) {
        return usersRestApi.findByKeycloakId(keycloakId).orElse(null);
    }

    public Set<Chat> getUserChats(Long userId) {
        return usersRestApi.getUserChats(userId);
    }

    public Boolean isUserInChat(Long userId, Long chatId) {
        return usersRestApi.isUserInChat(userId, chatId);
    }
}
