package ru.kors.socketbrokerservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kors.socketbrokerservice.api.ChatsRestApi;
import ru.kors.socketbrokerservice.api.UsersRestApi;
import ru.kors.socketbrokerservice.api.payload.UpdateChatPayload;
import ru.kors.socketbrokerservice.models.Chat;
import ru.kors.socketbrokerservice.models.ChatRole;
import ru.kors.socketbrokerservice.models.JoinRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatsRestApi chatsRestApi;
    private final UsersRestApi usersRestApi;

    public Set<Chat> findAll() {
        return chatsRestApi.findAll();
    }

    public Optional<Chat> findById(Long id) {
        return chatsRestApi.findById(id);
    }

    public Optional<Chat> findByTag(String tag) {
        return chatsRestApi.findByTag(tag);
    }

    public Set<Chat> findAllByName(String name) {
        return chatsRestApi.findAllByName(name);
    }

    public Chat updateChat(Long chatId, UpdateChatPayload updateChatPayload) {
        return chatsRestApi.updateChat(chatId, updateChatPayload);
    }

    public Set<ChatRole> getRoles(Long chatId, Long userId) {
        return chatsRestApi.getRoles(chatId, userId);
    }
}
