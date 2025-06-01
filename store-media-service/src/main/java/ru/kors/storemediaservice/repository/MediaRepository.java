package ru.kors.storemediaservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.kors.storemediaservice.models.Media;

public interface MediaRepository extends ReactiveCrudRepository<Media, Integer> {

    Mono<Media> findByKey(String key);

    @Query("SELECT key FROM files WHERE id = :id")
    Mono<String> findKeyById(@Param("id") Long id);
}
