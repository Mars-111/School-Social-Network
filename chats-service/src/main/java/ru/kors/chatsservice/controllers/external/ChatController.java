package ru.kors.chatsservice.controllers.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kors.chatsservice.controllers.external.Utils.CurrentUserUtil;
import ru.kors.chatsservice.controllers.external.dto.CreateChatDTO;
import ru.kors.chatsservice.controllers.internal.dto.ChangeChatDTO;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.ChatRole;
import ru.kors.chatsservice.models.entity.JoinRequest;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.services.*;

import java.util.List;

@RestController("chatController")
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController  {

    private final ChatService chatService;
    private final JoinRequestService joinRequestService;
    private final CurrentUserUtil currentUserUtil;
    private final ChatRoleService chatRoleService;
    private final KafkaProducerService kafkaProducerService;

    @GetMapping
    public List<Chat> getAllChats() {
        return chatService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable Long id) {
        Chat chat = chatService.findById(id);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/tag/{chatTag}")
    public ResponseEntity<Chat> getChatByTag(@PathVariable String chatTag) {
        Chat chat = chatService.findByTag(chatTag);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Chat>> getAllChatByName(@PathVariable String name) {
        List<Chat> chat = chatService.findAllByName(name);
        return ResponseEntity.ok(chat);
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<Chat> changeChat(@PathVariable Long chatId, @RequestBody ChangeChatDTO changeChatDTO) {
        if (!chatService.isOwner(chatId, currentUserUtil.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Chat chat = chatService.changeChat(chatId, changeChatDTO);
        return ResponseEntity.ok(chat);
    }

    @PostMapping
    public Chat createChat(@RequestBody CreateChatDTO chatDTO) {
        User user = currentUserUtil.getCurrentUser();
        return chatService.createChat(chatDTO, user);
    }

    @GetMapping("/{chatId}/join-request")
    public ResponseEntity<List<JoinRequest>> getAllJoinRequest(@PathVariable Long chatId) {
        if (!chatService.isOwner(chatId, currentUserUtil.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(joinRequestService.findByChatId(chatId));
    }

    @PostMapping("/{chatId}/join-request")
    public ResponseEntity<JoinRequest> createRequest(@PathVariable Long chatId) {
        User user = currentUserUtil.getCurrentUser();
        Chat chat = chatService.findById(chatId);
        JoinRequest request = joinRequestService.createJoinRequest(user, chat);

        //TODO: Отправка уведомления о запросах на вступление владельцу чата
         //personal event

        return ResponseEntity.ok(request);
    }

    @PostMapping("/join-request/{requestId}")
    public ResponseEntity<Void> acceptRequest(@PathVariable Long requestId) {
        User user = currentUserUtil.getCurrentUser();
        JoinRequest request = joinRequestService.findById(requestId);
        if (chatService.isOwner(request.getChat().getId(), user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        joinRequestService.acceptJoinRequest(request);

        //TODO: Отправка уведомления пользователю о принятии запроса

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/join-request/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestId) {
        JoinRequest request = joinRequestService.findById(requestId);
        if (!chatService.isOwner(request.getChat().getId(), currentUserUtil.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        joinRequestService.deleteById(requestId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
        if (!chatService.isOwner(chatId, currentUserUtil.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }

    //TODO: прродолжить ниже

    @PostMapping("/{chatId}/roles/{userId}")
    public ResponseEntity<Void> assignRole(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @RequestParam String role) {
        if (!chatService.isOwner(chatId, currentUserUtil.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        chatRoleService.assignRole(chatId, userId, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}/roles/{userId}")
    public ResponseEntity<List<ChatRole>> getRoles(
            @PathVariable Long chatId,
            @PathVariable Long userId) {
        List<ChatRole> roles = chatRoleService.getUserRoles(chatId, userId);
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{chatId}/roles/{userId}")
    public ResponseEntity<Void> unassignRole(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @RequestParam String role) {
        if (!chatService.isOwner(chatId, currentUserUtil.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //TODO: или админ
        chatRoleService.unassignRole(chatId, userId, role);
        return ResponseEntity.ok().build();
    }

}
