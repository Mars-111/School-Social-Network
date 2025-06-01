package ru.kors.chatsservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.Message;

import java.util.List;
import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByChat_Id(Long chatId, Pageable pageable);
    Set<Message> findAllByChat_Id(Long chatId);

//    List<Message> findByIdLessThan(Integer id, Pageable pageable);
//    List<Message> findAllByOrderByIdDesc(Pageable pageable);
}