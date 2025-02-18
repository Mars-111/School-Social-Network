package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.ReadMessage;
import ru.kors.chatsservice.models.entity.embeddable.ReadMessageId;

public interface ReadMessageRepository extends JpaRepository<ReadMessage, ReadMessageId> {
}