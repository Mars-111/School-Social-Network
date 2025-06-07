package ru.kors.storefileservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.kors.storefileservice.models.File;

public interface FileRepository extends ReactiveCrudRepository<File, Integer> {

    Mono<File> findByKey(String key);

    @Query("SELECT key FROM files WHERE id = :id")
    Mono<String> findKeyById(@Param("id") Long id);
}
