package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT cr.role FROM ChatRole cr WHERE cr.chat.id = ?1 AND cr.user.id = ?2")
    String findRoleByChat_IdAndUser_Id(Long chatId, Long userId);

    Long user(User user);


    @Query("""
    SELECT COUNT(cr) > 0
    FROM ChatRole cr
    WHERE cr.chat.id = :chatId
      AND cr.user.id = :userId
      AND (cr.role = :role1 OR cr.role = :role2)
    """)
    boolean existsByIdAndRoles(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId,
            @Param("role1") String role1,
            @Param("role2") String role2
    );

    @Query("""
    SELECT cr.role
    FROM ChatRole cr
    WHERE cr.chat.id = :chatId
      AND cr.user.id = :userId
      AND cr.role IN ('OWNER', 'ADMIN')
    ORDER BY
      CASE cr.role
        WHEN 'OWNER' THEN 0
        WHEN 'ADMIN' THEN 1
        ELSE 2
      END
    LIMIT 1
    """)
    String findAdminOrOwnerRole(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

}
