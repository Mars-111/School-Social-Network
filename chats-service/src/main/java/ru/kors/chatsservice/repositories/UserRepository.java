package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kors.chatsservice.models.entity.User;


import java.util.Optional;
import java.util.Set;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
    void deleteUserByKeycloakId(String keycloakId);

    Optional<User> findByTag(String tag);

    Boolean existsByTag(String tag);

    @Query("SELECT c.id FROM User u JOIN u.chats c WHERE u.id = :userId")
    Set<Long> findChatIdsByUserId(@Param("userId") Long userId);

    boolean existsByIdAndChats_Id(Long userId, Long chatId);
//    Boolean existsByUserIdAndChatId(Long userId, Long chatId);
}
