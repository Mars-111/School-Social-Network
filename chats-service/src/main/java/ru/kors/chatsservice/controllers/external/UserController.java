package ru.kors.chatsservice.controllers.external;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.kors.chatsservice.controllers.external.Utils.CurrentUserUtil;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.services.ChatService;
import ru.kors.chatsservice.services.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ChatService chatService;
    private final CurrentUserUtil currentUserUtil;

    @GetMapping
    public User getMe() {
        return userService.findById(currentUserUtil.getCurrentUser().getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<User> getUserByKeycloakId(@PathVariable String keycloakId) {
        User user = userService.findByKeycloakId(keycloakId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<User> getUserByTag(@PathVariable String tag) {
        User user = userService.findByTag(tag);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/tag/{tag}/exist")
    public ResponseEntity<Boolean> existUserByTag(@PathVariable String tag) {
        Boolean existed = userService.existUserByTag(tag);
        if (!existed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/chats")
    public ResponseEntity<Set<Chat>> getUserChats() {
        return ResponseEntity.ok(currentUserUtil.getCurrentUser().getChats());
    }

    @PostMapping("/chats/{chatId}/join")
    public ResponseEntity<Void> assignChatToUser(@PathVariable Long chatId) {
        User user = currentUserUtil.getCurrentUser();
        userService.assignChatToUser(user, chatId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chats/many/join")
    public ResponseEntity<Void> assignChatsToUser(@RequestParam Set<Long> chatsIds) {
        User user = currentUserUtil.getCurrentUser();
        userService.assignChatsToUser(user, chatsIds);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/chats/{chatId}/have")
    public ResponseEntity<Boolean> isUserInChat(@PathVariable Long chatId) {
        User user = currentUserUtil.getCurrentUser();

        // Если пользователь не состоит в чате, возвращаем 403 Forbidden с false
        if (!chatService.isUserInChat(chatId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false); // Запрещен доступ
        }

        // Если пользователь в чате, возвращаем true с 200 OK
        return ResponseEntity.ok(true);
    }
    //TODO: потом мб покрасивее isUserInChat



    @PostMapping
    public ResponseEntity<User> createUser(@AuthenticationPrincipal Jwt jwt) {
        log.info("------------------------------------");
        return ResponseEntity.ok(userService.createUser(jwt));
    }



}
