package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kors.chatsservice.models.entity.MediaMetadata;

import java.util.List;

public interface MediaMessageMetadataRepository extends JpaRepository<MediaMetadata, Long> {
    @Query("SELECT m.mediaId FROM MediaMetadata m WHERE m.message.id = :messageId")
    List<Long> findMediaIdByMessageId(@Param("messageId") Long messageId);
}
