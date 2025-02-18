package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.ChatRole;
import ru.kors.chatsservice.models.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChatRoleRepository extends JpaRepository<ChatRole, Long> {
    Optional<ChatRole> findByChat_IdAndUser_IdAndRole(Long chatId, Long userId, String role);
    void deleteByChat_IdAndUser_IdAndRole(Long chatId, Long userId, String role);
    void deleteAllByChat_IdAndUser_Id(Long chatId, Long userId);
    List<ChatRole> findAllByChat_IdAndUser_Id(Long chatId,  Long userId);

    Long user(User user);
}
