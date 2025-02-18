package ru.kors.chatsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.exceptions.BadRequestException;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.ChatRole;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.repositories.ChatRoleRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoleService {
    private final ChatRoleRepository chatRoleRepository;
    private final UserService userService;
    private final ChatService chatService;

    public ChatRole findById(Long id) {
        return chatRoleRepository.findById(id).orElse(null);
    }

    public void assignRole(Long chatId, Long userId, String role) {
        if (role.isEmpty()) {
            throw new BadRequestException("Role is empty");
        }
        User user = userService.findById(userId);
        Chat chat = chatService.findById(chatId);

        ChatRole chatRole = new ChatRole();
        chatRole.setUser(user);
        chatRole.setChat(chat);
        chatRole.setRole(role);

        chatRoleRepository.save(chatRole);
    }

    public List<ChatRole> getUserRoles(Long chatId, Long userId) {
        return chatRoleRepository.findAllByChat_IdAndUser_Id(chatId, userId);
    }

    public void unassignRole(Long chatId, Long userId, String role) {
        chatRoleRepository.deleteByChat_IdAndUser_IdAndRole(chatId, userId, role);
    }
}
