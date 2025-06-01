package ru.kors.storemediaservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.kors.storemediaservice.exceptions.NotAccessToMediaException;
import ru.kors.storemediaservice.models.DecodeTokenResult;
import ru.kors.storemediaservice.services.CurrentUserUtil;
import ru.kors.storemediaservice.services.MediaService;
import ru.kors.storemediaservice.services.MediaAccessTokenService;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.time.Duration;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final S3AsyncClient s3AsyncClient;
    private final MediaService mediaService;
    private final CurrentUserUtil currentUserUtil;
    private final MediaAccessTokenService mediaAccessTokenService;

    @Value("${s3.bucket}")
    private String bucket;

    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadMultiple(@RequestPart("file") Mono<FilePart> file,
                                                       @RequestParam("size") Long fileSize) {
        return file.flatMap(filePart -> mediaService.upload(filePart, fileSize))
                .map(uploadResult ->
                        mediaAccessTokenService.generateMediaTokenForCreate(
                                uploadResult.getMedia().getId(),
                                uploadResult.getMedia().getOwnerId(),
                                uploadResult.getMedia().getExtension(),
                                uploadResult.getMedia().getFilename(),
                                uploadResult.getImageMetadata(),
                                fileSize))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{mediaId}")
    public Mono<ResponseEntity<?>> download(@PathVariable Long mediaId,
                                               @RequestHeader("Access-Media-Token") String mediaToken) {
        DecodeTokenResult decoded = mediaAccessTokenService.decode(mediaToken);

        return currentUserUtil.getCurrentUserId()
                .handle((currentUserId, sink) -> {
                    if (!currentUserId.equals(decoded.userId())) {
                        sink.error(new NotAccessToMediaException("Token userId is not equal to current userId"));
                        return;
                    }
                    if (!decoded.allowedMediaIds().contains(mediaId)) {
                        sink.error(new NotAccessToMediaException("Not access to media"));
                        return;
                    }
                    sink.complete();
                })
                .flatMap( userId -> mediaService.findKeyById(mediaId))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found")))
                .flatMap(key ->
                    mediaService.getOneTimeLink(key, Duration.ofSeconds(90))
                        .map(url -> ResponseEntity
                                .status(HttpStatus.FOUND)
                                .header(HttpHeaders.LOCATION, url)
                                .build())
                );
    }




//
//    @GetMapping("/download/{key}")
//    public ResponseEntity<byte[]> download(@PathVariable String key) {
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucket)
//                .key(key)
//                .build();
//
//        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
//            byte[] bytes = response.readAllBytes();
//
//            String contentType = response.response().contentType();
//            long contentLength = response.response().contentLength();
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.parseMediaType(contentType))
//                    .contentLength(contentLength)
//                    .body(bytes);
//
//        } catch (IOException e) {
//            return ResponseEntity.status(500).body(("Download failed: " + e.getMessage()).getBytes());
//        }
//    }
}
