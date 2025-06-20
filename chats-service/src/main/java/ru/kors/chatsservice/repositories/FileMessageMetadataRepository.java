package ru.kors.chatsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kors.chatsservice.models.entity.FileMetadata;

import java.util.List;

public interface FileMessageMetadataRepository extends JpaRepository<FileMetadata, Long> {
    @Query("SELECT m.fileId FROM FileMetadata m WHERE m.message.id = :messageId")
    List<Long> findFileIdsByMessageId(@Param("messageId") Long messageId);
}
