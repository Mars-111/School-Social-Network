package ru.kors.storemediaservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.kors.storemediaservice.models.Media;

public interface MediaRepository extends ReactiveCrudRepository<Media, Integer> {

    Mono<Media> findByKey(String key);
}
