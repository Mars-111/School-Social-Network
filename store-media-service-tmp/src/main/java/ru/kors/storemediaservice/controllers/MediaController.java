package ru.kors.storemediaservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kors.storemediaservice.services.KafkaProducerService;
import ru.kors.storemediaservice.services.MediaService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {
    private final MediaService  mediaService;
    private final KafkaProducerService kafkaProducerService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam("file") MultipartFile file) {
        String fileUuid = UUID.randomUUID().toString();

        executorService.submit(() -> {
            try {
                mediaService.upload(fileUuid, file);
            } catch (Exception e) {
                log.error("Upload failed", e);
            } finally {
                //Отправляем то, что файл готов
                kafkaProducerService.sendFileStatusNotification(-1L, "SUCCESS");

            }
        });

        return ResponseEntity.ok("Successes");
    }

    @GetMapping("/download-url")
    public ResponseEntity<String> downloadUrl(@RequestParam("key") String key) {
        URL url = mediaService.generateDownloadUrl(key, Duration.ofMinutes(10));
        return ResponseEntity.ok(url.toString());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("key") String key) {
        mediaService.delete(key);
        return ResponseEntity.ok("Deleted: " + key);
    }
}
