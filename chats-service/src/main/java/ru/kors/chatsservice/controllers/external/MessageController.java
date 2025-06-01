package ru.kors.chatsservice.controllers.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kors.chatsservice.controllers.external.Utils.CurrentUserUtil;
import ru.kors.chatsservice.controllers.external.dto.CreateMessageDTO;
import ru.kors.chatsservice.controllers.external.dto.UpdateMessageDTO;
import ru.kors.chatsservice.models.entity.Message;
import ru.kors.chatsservice.services.KafkaProducerService;
import ru.kors.chatsservice.services.MessageService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final CurrentUserUtil currentUserUtil;

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.findAll();
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long messageId) {
        Message message = messageService.findById(messageId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<Set<Message>> getMessagesByChatId(@PathVariable Long chatId) {
        Set<Message> messages = messageService.findAllByChatId(chatId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody CreateMessageDTO messageDTO) {
        Message message = messageService.createMessage(messageDTO, currentUserUtil.getCurrentUserId());
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long messageId, @RequestBody UpdateMessageDTO messageDTO) {
        Message updatedMessage =
                messageService.updateMessage(messageId, messageDTO, currentUserUtil.getCurrentUserId());
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{messageId}/media/access-jwt")
    public ResponseEntity<String> getMediaAccessJwt(@PathVariable Long messageId) {
        return ResponseEntity.ok(messageService.getMediaAccessJwt(messageId, currentUserUtil.getCurrentUserId()));
    }
}
