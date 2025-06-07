package ru.kors.chatsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.exceptions.BadRequestException;
import ru.kors.chatsservice.exceptions.DoesNotHaveAccessException;
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


    private boolean isValidRole(String role) {
        return !role.equalsIgnoreCase("OWNER");
    }

    public void assignRole(Long chatId, Long userId, String role, Long executorId) {
        if (role.isEmpty()) {
            throw new BadRequestException("Role is empty");
        }
        if (!isValidRole(role)) {
            throw new BadRequestException("Invalid role: " + role);
        }
        String userRole = chatRoleRepository.findRoleByChat_IdAndUser_Id(chatId, executorId);
        if (userRole == null) {
            throw new DoesNotHaveAccessException("User does not have a role in this chat");
        }
        if (!userRole.equals("OWNER") && (role.equals("OWNER") || role.equals("ADMIN"))) {
            throw new DoesNotHaveAccessException("User does not have permission to assign this role");
        }
        boolean isUserInChat = userService.existsByIdAndChatId(userId, chatId);
        if (!isUserInChat) {
            throw new BadRequestException("the user is not in the chat");
        }

        User user = new User();
        user.setId(userId);
        Chat chat = new Chat();
        chat.setId(chatId);

        ChatRole chatRole = new ChatRole();
        chatRole.setUser(user);
        chatRole.setChat(chat);
        chatRole.setRole(role);

        chatRoleRepository.save(chatRole);
    }

    public List<ChatRole> getUserRoles(Long chatId, Long userId) {
        return chatRoleRepository.findAllByChat_IdAndUser_Id(chatId, userId);
    }

    public void unassignRole(Long chatId, Long userId, String role, Long executorId) {
        if (role.isEmpty()) {
            throw new BadRequestException("Role is empty");
        }
        if (!isValidRole(role)) {
            throw new BadRequestException("Invalid role: " + role);
        }
        String executorTopRole = chatRoleRepository.findAdminOrOwnerRole(chatId, executorId);
        if (executorTopRole == null || (executorTopRole.equals("ADMIN") && (role.equals("OWNER") || role.equals("ADMIN")))) {
            throw new DoesNotHaveAccessException("User does not have permission to unassign this role");
        }
        chatRoleRepository.deleteByChat_IdAndUser_IdAndRole(chatId, userId, role);
    }
}
