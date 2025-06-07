package ru.kors.chatsservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kors.chatsservice.models.entity.Message;

import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByChat_Id(Long chatId, Pageable pageable);
    Set<Message> findAllByChat_Id(Long chatId);

    @Query("SELECT MAX(m.timelineId) FROM Message m WHERE m.chat.id = :chatId")
    Integer findMaxTimelineIdByChatId(@Param("chatId") long chatId);

//    List<Message> findByIdLessThan(Integer id, Pageable pageable);
//    List<Message> findAllByOrderByIdDesc(Pageable pageable);
}