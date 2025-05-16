package ru.kors.storemediaservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.kors.storemediaservice.models.Media;
import ru.kors.storemediaservice.repository.MediaRepository;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;


    public Mono<Media> save(Media media) {
        return mediaRepository.save(media);
    }

    public Mono<Media> findById(Integer id) {
        return mediaRepository.findById(id);
    }

    public Mono<Media> findByKey(String key) {
        return mediaRepository.findByKey(key);
    }
}
