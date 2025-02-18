package ru.kors.chatsservice.controllers.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kors.chatsservice.controllers.internal.dto.CreateUserDTO;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.services.ChatService;
import ru.kors.chatsservice.services.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/internal/api/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;
    private final ChatService chatService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<User> getUserByKeycloakId(@PathVariable String keycloakId) {
        User user = userService.findByKeycloakId(keycloakId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<User> getUserByTag(@PathVariable String tag) {
        User user = userService.findByTag(tag);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/chats")
    public ResponseEntity<Set<Chat>> getUserChats(@PathVariable Long id) {
        Set<Chat> chats = userService.findUserChats(id);
        if (chats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(chats);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserDTO user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/chats/assign")
    public ResponseEntity<Void> assignChatsToUser(@PathVariable Long userId, @RequestBody Set<Long> chatsIds) {
        User user = userService.findById(userId);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        userService.assignChatsToUser(user, chatsIds);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/chats/{chatId}/have")
    public ResponseEntity<Boolean> isUserInChat(@PathVariable Long userId, @PathVariable Long chatId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Chat chat = chatService.findById(chatId);
        if (chat == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user.getChats().contains(chat));
    }


}