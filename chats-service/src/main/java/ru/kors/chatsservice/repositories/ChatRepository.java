package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.Chat;

import java.util.List;
import java.util.Optional;


public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByName(String chatName);

    Optional<Chat> findByTag(String tag);

    List<Chat> findAllByName(String name);

    List<Chat> findAllByOwner_Id(Long ownerId);

    boolean existsByIdAndUsers_Id(Long chatId, Long usersId);

    boolean existsByTag(String tag);
}