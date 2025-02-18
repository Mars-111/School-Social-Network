package ru.kors.chatsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.models.entity.ChatEvent;
import ru.kors.chatsservice.repositories.ChatEventRepository;

@Service
@RequiredArgsConstructor
public class ChatEventService {
    private final ChatEventRepository chatEventRepository;

    public ChatEvent save(ChatEvent chatEvent) {
        return chatEventRepository.save(chatEvent);
    }

}
