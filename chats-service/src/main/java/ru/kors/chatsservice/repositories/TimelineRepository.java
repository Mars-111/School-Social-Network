package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kors.chatsservice.models.entity.Timeline;
import ru.kors.chatsservice.models.entity.ids.TimelineId;

public interface TimelineRepository extends JpaRepository<Timeline, TimelineId> {
}
