package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.MessageMedia;

public interface MessageMediaRepository extends JpaRepository<MessageMedia, Long> {
}
