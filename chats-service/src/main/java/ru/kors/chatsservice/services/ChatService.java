package ru.kors.chatsservice.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.controllers.external.dto.CreateChatDTO;
import ru.kors.chatsservice.controllers.internal.dto.ChangeChatDTO;
import ru.kors.chatsservice.exceptions.BadRequestException;
import ru.kors.chatsservice.exceptions.NotFoundEntityException;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.ChatEvent;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.repositories.ChatRepository;
import ru.kors.chatsservice.repositories.ChatRoleRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoleRepository chatRoleRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ChatEventService chatEventService;

    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(() -> new NotFoundEntityException("Chat not found"));
    }

    public Chat findByTag(String chatTag) {
        return chatRepository.findByTag(chatTag).orElseThrow(() -> new NotFoundEntityException("Chat not found"));
    }

    public Chat createChat(CreateChatDTO chatDTO, User owner) {
        if (chatDTO == null || owner == null) {
            throw new BadRequestException("Invalid data in saveChat method.");
        }

        // Проверка, существует ли уже чат с таким тегом
        if (chatRepository.existsByTag(chatDTO.tag())) {
            throw new BadRequestException("Chat with this tag already exists.");
        }

        Chat chat = new Chat();
        chat.setTag(chatDTO.tag());
        chat.setName(chatDTO.name());
        chat.setPrivateChat(chatDTO.privateChat());
        chat.setOwner(owner);
        chat.getUsers().add(owner);

        chat = chatRepository.save(chat);

        kafkaProducerService.sendCreateChat(chat);

        return chat;
    }


    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }


    public Chat changeChat(Long chatId, ChangeChatDTO chatDetails) {
        // Получаем чат из БД
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundEntityException("Chat not found"));

        // Создаем событие изменения чата
        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setType("change chat");
        chatEvent.setChat(chat);

        ObjectMapper mapper = new ObjectMapper();
        // Массив для хранения изменений
        ArrayNode changes = mapper.createArrayNode();

        // Если изменяется tag
        if (chatDetails.tag() != null) {
            String oldTag = chat.getTag();
            chat.setTag(chatDetails.tag()); //set
            ObjectNode changeNode = mapper.createObjectNode();
            changeNode.put("field", "tag");
            changeNode.put("old_value", oldTag);
            changeNode.put("new_value", chatDetails.tag());
            changes.add(changeNode);
        }

        // Если изменяется name
        if (chatDetails.name() != null) {
            String oldName = chat.getName();
            chat.setName(chatDetails.name()); //set
            ObjectNode changeNode = mapper.createObjectNode();
            changeNode.put("field", "name");
            changeNode.put("old_value", oldName);
            changeNode.put("new_value", chatDetails.name());
            changes.add(changeNode);
        }

        // Если изменяется privateChat
        if (chatDetails.privateChat() != null) {
            // Приводим Boolean к строке для удобства
            String oldPrivate = chat.getPrivateChat() != null ? chat.getPrivateChat().toString() : null;
            String newPrivate = chatDetails.privateChat().toString();
            chat.setPrivateChat(chatDetails.privateChat()); //set
            ObjectNode changeNode = mapper.createObjectNode();
            changeNode.put("field", "privateChat");
            changeNode.put("old_value", oldPrivate);
            changeNode.put("new_value", newPrivate);
            changes.add(changeNode);
        }

        if (changes.isEmpty()) {
            throw new BadRequestException("No changes in chat.");
        }

        // Устанавливаем сформированный массив изменений в data события
        chatEvent.setData(changes);

        chatEvent = chatEventService.save(chatEvent);

        // Отправляем событие в Kafka
        kafkaProducerService.send(chatEvent);

        // Сохраняем обновленный чат и возвращаем его
        return chatRepository.save(chat);
    }


    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
    }

    public List<Chat> findAllById(Collection<Long> chatIds) {
        return chatRepository.findAllById(chatIds);
    }

    public Chat getChatByName(String chatName) {
        return chatRepository.findByName(chatName).orElseThrow(() -> new NotFoundEntityException("Chat not found"));
    }

    public List<Chat> findAllByName(String name) {
        return chatRepository.findAllByName(name);
    }

    public Boolean isOwner(Long chatId, Long userId) {
        Chat chat = findById(chatId);
        return chat.getOwner().getId().equals(userId);
    }

    public boolean isUserInChat(Long chatId, Long userId) {
        return chatRepository.existsByIdAndUsers_Id(chatId, userId);
    }
}