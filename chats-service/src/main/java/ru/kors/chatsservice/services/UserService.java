package ru.kors.chatsservice.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.controllers.internal.dto.CreateUserDTO;
import ru.kors.chatsservice.exceptions.BadRequestException;
import ru.kors.chatsservice.exceptions.NotFoundEntityException;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.ChatEvent;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.repositories.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ChatService chatService;
    private final KafkaProducerService kafkaProducerService;
    private final ChatEventService chatEventService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundEntityException("User not found"));
    }

    public User findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new NotFoundEntityException("User not found"));
    }

    public User createUser(CreateUserDTO userDTO) {
        User user = new User();
        user.setKeycloakId(userDTO.keycloakId());
        user.setTag(userDTO.tag());
        return userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void deleteByKeycloakId(String keycloakId) {
        userRepository.deleteUserByKeycloakId(keycloakId);
    }

    public Set<Chat> findChatsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundEntityException("User not found"));
        return user.getChats();
    }

    public void assignChatToUser(User user, Long chatId) {
        Chat chat = chatService.findById(chatId);
        if (chat.getPrivateChat()) {
            //TODO
            throw new BadRequestException("This chat is private");
        }

        user.getChats().add(chat);

        ChatEvent event = new ChatEvent();
        event.setType("join");
        event.setChat(chat);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("user_id", user.getId());

        event.setData(objectNode);


        log.info("event info: (chat id: {}), (type: {}), (data: {}).", event.getChat().getId(), event.getType(), event.getData());
        log.info("Saving event with JSON data: {}", event.getData().toString());
        chatEventService.save(event);


        kafkaProducerService.send(event);
    }

    public void assignChatsToUser(User user, Set<Long> chatsIds) {
        List<Chat> chats = chatService.findAllById(chatsIds);
        if (chats.size() != chatsIds.size()) {
            throw new NotFoundEntityException("Some chats not found");
        }
        user.getChats().addAll(chats);
        userRepository.save(user);
    } //TODO: Позже доделать

    public User findByTag(String tag) {
        return userRepository.findByTag(tag).orElseThrow(() -> new NotFoundEntityException("User not found"));
    }

    public Boolean existUserByTag(String tag) {
        return userRepository.existsByTag(tag);
    }

    public Set<Long> findChatsIdsByUserId(Long userId) {
        return userRepository.findChatIdsByUserId(userId);
    }
}