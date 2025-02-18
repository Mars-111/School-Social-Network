package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.UserEvent;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
}
