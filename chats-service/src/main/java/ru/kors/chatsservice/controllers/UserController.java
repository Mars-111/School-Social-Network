package ru.kors.chatsservice.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kors.chatsservice.controllers.payload.AssignChatsToUserPayload;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.services.ChatService;
import ru.kors.chatsservice.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserByIdController {

    private final UserService userService;
    private final ChatService chatService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestParam(required = false) Long id,
                                        @RequestParam(required = false) String keycloakId) {
        User user = userService.getUserById(id);
        if (user == null) {
            user = userService.getUserByKeycloakId(keycloakId);
        }

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/keycloak/{keycloakId}")
    public ResponseEntity<Void> deleteUserByKeycloakId(@PathVariable String keycloakId) {
        userService.deleteUserByKeycloakId(keycloakId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign-chats")
    public ResponseEntity<User> assignChatsToUser(@RequestBody AssignChatsToUserPayload payload) {
        User user = userService.getUserById(payload.userId());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Найти чаты по их идентификаторам
        List<Chat> chats = chatService.findAllById(payload.chatsId());
        if (chats.size() != payload.chatsId().size()) {
            return ResponseEntity.badRequest().build();
        }

        // Добавить найденные чаты к пользователю
        user.getChats().addAll(chats);

        // Сохранить обновленного пользователя
        User updatedUser = userService.createUser(user);

        return ResponseEntity.ok(updatedUser);
    }
}