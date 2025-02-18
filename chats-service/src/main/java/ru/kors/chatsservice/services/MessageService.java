package ru.kors.chatsservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.controllers.external.Utils.CurrentUserUtil;
import ru.kors.chatsservice.controllers.external.dto.CreateMessageDTO;
import ru.kors.chatsservice.controllers.external.dto.UpdateMessageDTO;
import ru.kors.chatsservice.exceptions.BadRequestException;
import ru.kors.chatsservice.exceptions.DoesNotHaveAccessException;
import ru.kors.chatsservice.exceptions.NotFoundEntityException;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.ChatEvent;
import ru.kors.chatsservice.models.entity.Message;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.models.entity.serializers.ChatSerializer;
import ru.kors.chatsservice.repositories.ChatRepository;
import ru.kors.chatsservice.repositories.MessageRepository;
import ru.kors.chatsservice.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final CurrentUserUtil currentUserUtil;
    private final ChatService chatService;
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final ChatEventService chatEventService;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public Message findById(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new NotFoundEntityException("Message not found"));
    }

    public Page<Message> findAllByChatId(Long chatId, Pageable pageable) {
        return messageRepository.findAllByChat_Id(chatId, pageable);
    }

    public List<Message> findAllByChatId(Long chatId) {
        return messageRepository.findAllByChat_Id(chatId);
    }

    public Message createMessage(CreateMessageDTO messageDTO, Long senderId) {
        //Проверка наличия чата у пользователя
        userService.findUserChats(senderId).stream()
                .map(chat -> chat.getId().equals(messageDTO.chatId()))
                .findFirst().orElseThrow(() -> new DoesNotHaveAccessException("User does not have access to chat"));
        Message message = new Message();
        message.setType(messageDTO.type());
        message.setChat(chatService.findById(messageDTO.chatId()));
        message.setContent(messageDTO.content());
        message.setSender(userService.findById(senderId));

        message = messageRepository.save(message);

        kafkaProducerService.send(message);

        return message;
    }

    public Message updateMessage(Long messageId, UpdateMessageDTO updateMessageDTO) {
        Message message = findById(messageId);

        // Создаем событие изменения чата
        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setType("change message");
        chatEvent.setChat(message.getChat());

        ObjectMapper mapper = new ObjectMapper();
        // Массив для хранения изменений

        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("message_id", message.getId());

        ArrayNode changes = mapper.createArrayNode();

        if (updateMessageDTO.type() != null) {
            String oldType = message.getType();
            message.setType(updateMessageDTO.type()); //set
            ObjectNode changeNode = mapper.createObjectNode();
            changeNode.put("field", "type");
            changeNode.put("old_value", oldType);
            changeNode.put("new_value", message.getType());
            changes.add(changeNode);
        }
        if (updateMessageDTO.content() != null) {
            String oldContent = message.getContent();
            message.setContent(updateMessageDTO.content()); //set
            ObjectNode changeNode = mapper.createObjectNode();
            changeNode.put("field", "content");
            changeNode.put("old_value", oldContent);
            changeNode.put("new_value", message.getContent());
            changes.add(changeNode);
        }

        if (changes.size() < 2) {
            throw new BadRequestException("No changes in message.");
        }

        objectNode.put("changes", changes);

        chatEvent.setData(objectNode);

        message.getChat().getEvents().add(chatEvent);

        kafkaProducerService.send(chatEvent);

        return messageRepository.save(message);
    }

    public void deleteMessage(Long messageId) {
        Message message = findById(messageId);

        if (message.getSender() != currentUserUtil.getCurrentUser() || !currentUserUtil.thisUserIsOwnerChat(message.getChat())) {
            throw new DoesNotHaveAccessException("User does not have access to message");
        }

        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setType("delete message");
        chatEvent.setChat(message.getChat());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("message_id", messageId);

        chatEvent.setData(objectNode);

        kafkaProducerService.send(chatEventService.save(chatEvent));

        messageRepository.deleteById(messageId);
    }
}