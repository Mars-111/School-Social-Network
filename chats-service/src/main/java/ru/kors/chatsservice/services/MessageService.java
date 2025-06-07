package ru.kors.chatsservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.controllers.external.Utils.CurrentUserUtil;
import ru.kors.chatsservice.controllers.external.dto.CreateMessageDTO;
import ru.kors.chatsservice.controllers.external.dto.UpdateMessageDTO;
import ru.kors.chatsservice.exceptions.BadRequestException;
import ru.kors.chatsservice.exceptions.DoesNotHaveAccessException;
import ru.kors.chatsservice.exceptions.NotFoundEntityException;
import ru.kors.chatsservice.models.AccessFileJWT;
import ru.kors.chatsservice.models.entity.*;
import ru.kors.chatsservice.models.entity.constants.MessageFlags;
import ru.kors.chatsservice.models.entity.constants.TimelineTypes;
import ru.kors.chatsservice.models.entity.ids.TimelineId;
import ru.kors.chatsservice.repositories.FileMessageMetadataRepository;
import ru.kors.chatsservice.repositories.MessageRepository;
import ru.kors.chatsservice.repositories.TimelineRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final CurrentUserUtil currentUserUtil;
    private final ChatService chatService;
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final ChatEventService chatEventService;
    private final FileJWTService fileJWTService;
    private final TimelineService timelineService;
    private final TimelineRepository timelineRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public Message findById(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new NotFoundEntityException("Message not found"));
    }

    public Page<Message> findAllByChatId(Long chatId, Pageable pageable) {
        return messageRepository.findAllByChat_Id(chatId, pageable);
    }

    public Set<Message> findAllByChatId(Long chatId) {
        return messageRepository.findAllByChat_Id(chatId);
    }

    public Message createMessage(CreateMessageDTO messageDTO, Long senderId) {
        log.info("Message reply_to: {}", messageDTO.replyToId());

        //Проверка наличия чата у пользователя
        if (!userService.existsByIdAndChatId(senderId, messageDTO.chatId())) { //TODO: Добавить кэширование в редис по типу флагов (1111) первая единица - пользовать есть в чате, вторая - пользователь в муте и тд
            throw new DoesNotHaveAccessException("User does not have access to chat");
        }

        if (messageDTO.replyToId() != null && messageDTO.forwardedFromId() != null) {
            throw new BadRequestException("You cannot reply and forward message at the same time");
        }



        Message message = new Message();

        if (messageDTO.fileTokens() != null) {
            List<AccessFileJWT> fileTokens = new ArrayList<>();
            for (String i : messageDTO.fileTokens()) {
                var accessFileJWT = fileJWTService.convertAccessFileJWT(i);
                if (!Objects.equals(senderId, accessFileJWT.userId()) || !accessFileJWT.subject().equals("file_create")) {
                    throw new DoesNotHaveAccessException("User does not have access to added file");
                }
                fileTokens.add(accessFileJWT);
            }
            for (AccessFileJWT jwt : fileTokens) {
                var fileMetadata = new FileMetadata(jwt);
                message.addFile(fileMetadata); // ← тут и message задается, и добавляется в коллекцию
            }
            message.setFlags(MessageFlags.setFlag(message.getFlags(), MessageFlags.HAS_FILE));
        }


        if (messageDTO.flags() != null) {
            message.setFlags(MessageFlags.sortFlagsDefaultUserToCreateMessage(messageDTO.flags()));
        }
        message.setType(messageDTO.type());
        message.setChat(chatService.findById(messageDTO.chatId()));
        if (messageDTO.content() != null && !messageDTO.content().isEmpty()) {
            message.setContent(messageDTO.content());
            message.setFlags(MessageFlags.setFlag(message.getFlags(), MessageFlags.HAS_TEXT));
        }
        User sender = entityManager.getReference(User.class, senderId);
        message.setSender(sender);
        if (messageDTO.replyToId() != null) {
            Message replyTo = findById(messageDTO.replyToId());
            if (replyTo.getChat().getId() != messageDTO.chatId()) {
                throw new BadRequestException("Reply to message is not in the same chat");
            }
            if (MessageFlags.isFlagSet(replyTo.getFlags(), MessageFlags.IS_MUTED)) {
                throw new BadRequestException("You cannot reply to muted message");
            }
            message.setReplyTo(replyTo);
            message.setFlags(MessageFlags.setFlag(message.getFlags(), MessageFlags.IS_REPLY));
        }
        if (messageDTO.forwardedFromId() != null) {
            Message forwardedFrom = entityManager.getReference(Message.class, messageDTO.forwardedFromId());
            message.setForwardedFrom(forwardedFrom);
            message.setFlags(MessageFlags.setFlag(message.getFlags(), MessageFlags.IS_FORWARDED));
        }

        if ((
                (message.getFlags() & MessageFlags.HAS_TEXT) |
                (message.getFlags() & MessageFlags.HAS_FILE) |
                (message.getFlags() & MessageFlags.IS_FORWARDED)
            ) == 0) {
            throw new BadRequestException("Empty message");
        }

        // Получение порядкового номера сообщения в чате TIMELINE

        Integer timelineId = timelineService.getNextOrderId(messageDTO.chatId());
        TimelineId timelineIdObj = new TimelineId(messageDTO.chatId(), timelineId);
        Timeline timeline = new Timeline(timelineIdObj, TimelineTypes.MESSAGE_TYPE);
        timelineRepository.save(timeline);
        message.setTimelineId(timeline.getId().getTimelineId());

        //

        message = messageRepository.save(message);

        kafkaProducerService.send(message);

        return message;
    }

    public Message updateMessage(Long messageId, UpdateMessageDTO updateMessageDTO, Long senderRequestId) {
        Message message = findById(messageId);

        if (!message.getSender().getId().equals(senderRequestId)) {
            throw new DoesNotHaveAccessException("User does not have access to message");
        }

        // Создаем событие изменения чата
        ChatEvent chatEvent = new ChatEvent();

        chatEvent.setType("change message");
        chatEvent.setChat(message.getChat());

        ObjectMapper mapper = new ObjectMapper();
        // Массив для хранения изменений

        ObjectNode objectNode = mapper.createObjectNode();

        objectNode.put("message_id", message.getId());

        ArrayNode changes = mapper.createArrayNode();


        if (updateMessageDTO.content() != null) {
            String oldContent = message.getContent();
            message.setContent(updateMessageDTO.content()); //set
            ObjectNode changeNode = mapper.createObjectNode();
            changeNode.put("field", "content");
            changeNode.put("old_value", oldContent);
            changeNode.put("new_value", message.getContent());
            changes.add(changeNode);
        }

        objectNode.put("changes", changes);

        chatEvent.setData(objectNode);

        // Получение порядкового номера сообщения в чате TIMELINE

        Integer timelineId = timelineService.getNextOrderId(message.getChat().getId());
        TimelineId timelineIdObj = new TimelineId(message.getChat().getId(), timelineId);
        Timeline timeline = new Timeline(timelineIdObj, TimelineTypes.EVENT_TYPE);
        timelineRepository.save(timeline);
        chatEvent.setTimelineId(timeline.getId().getTimelineId());

        //

        message.getChat().getEvents().add(chatEvent);

        message.setFlags(MessageFlags.setFlag(message.getFlags(), MessageFlags.IS_EDITED));

        kafkaProducerService.send(chatEvent);

        return messageRepository.save(message);
    }

    public void deleteMessage(Long messageId) {
        Message message = findById(messageId);

        if (message.getSender() != currentUserUtil.getCurrentUser() && !currentUserUtil.thisUserIsOwnerChat(message.getChat())) {
            throw new DoesNotHaveAccessException("User does not have access to message");
        }

        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setType("delete message");
        chatEvent.setChat(message.getChat());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("message_id", messageId);

        chatEvent.setData(objectNode);

        message.setFlags(MessageFlags.setFlag(message.getFlags(), MessageFlags.IS_DELETED));

        // Получение порядкового номера сообщения в чате TIMELINE

        Integer timelineId = timelineService.getNextOrderId(message.getChat().getId());
        TimelineId timelineIdObj = new TimelineId(message.getChat().getId(), timelineId);
        Timeline timeline = new Timeline(timelineIdObj, TimelineTypes.EVENT_TYPE);
        timelineRepository.save(timeline);
        chatEvent.setTimelineId(timeline.getId().getTimelineId());

        //

        kafkaProducerService.send(chatEventService.save(chatEvent));
    }

    public String getFileAccessJwt(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new DoesNotHaveAccessException("Message not found"));
        if (!chatService.isUserInChat(message.getChat().getId(), userId)) {
            throw new DoesNotHaveAccessException("User does not have access to message");
        }

        return fileJWTService.generateFileAccessToken(
                new HashSet<>(message.getFileList().stream().map(FileMetadata::getFileId).toList()),
                userId,
                180);
    }
}