package ru.kors.storemediaservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.kors.storemediaservice.models.Media;
import ru.kors.storemediaservice.services.CurrentUserUtil;
import ru.kors.storemediaservice.services.MediaService;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final S3AsyncClient s3AsyncClient;
    private final MediaService mediaService;
    private final CurrentUserUtil currentUserUtil;

    @Value("${s3.bucket}")
    private String bucket;

//    @PostMapping("/upload")
//    public Mono<ResponseEntity<?>> uploadMultiple(@RequestPart("files") Mono<FilePart> files,
//                                                  @RequestPart("length") Long length) {
//        return files.flatMap(filePart -> {
//
//                    if (length == null || length <= 0) {
//                        return Mono.just(Map.of(
//                                "filename", filePart.filename(),
//                                "status", "error",
//                                "message", "Content-Length header is missing or invalid"
//                        ));
//                    }
//
//                    String key = UUID.randomUUID() + "-" + filePart.filename();
//
//                    AsyncRequestBody requestBody = AsyncRequestBody.fromPublisher(
//                            filePart.content()
//                                    .map(dataBuffer -> {
//                                        ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
//                                        DataBufferUtils.release(dataBuffer);
//                                        return byteBuffer;
//                                    })
//                    );
//
//                    PutObjectRequest request = PutObjectRequest.builder()
//                            .bucket(bucket)
//                            .key(key)
//                            .contentType(Objects.requireNonNull(filePart.headers().getContentType()).toString())
//                            .contentLength(length) // Указываем длину здесь
//                            .build();
//
//                    return Mono.fromFuture(() -> s3AsyncClient.putObject(request, requestBody))
//                            .map(resp -> Map.of(
//                                    "filename", filePart.filename(),
//                                    "key", key,
//                                    "status", "success"
//                            ))
//                            .onErrorResume(e -> Mono.just(Map.of(
//                                    "filename", filePart.filename(),
//                                    "key", key,
//                                    "status", "error",
//                                    "message", e.getMessage()
//                            )));
//                })
//                .map(ResponseEntity::ok);
//    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<?>> uploadMultiple(@RequestPart("files") Mono<FilePart> files,
                                                  @RequestParam("length") Long length) {
        return files.flatMap(filePart -> {

            if (length == null || length <= 0) {
                return Mono.just(ResponseEntity.badRequest().body(Map.of(
                        "filename", filePart.filename(),
                        "status", "error",
                        "message", "Content-Length header is missing or invalid"
                )));
            }

            String key = UUID.randomUUID() + "-" + filePart.filename();

            Flux<ByteBuffer> byteBufferFlux = filePart.content() //Гпт сказал что тут потонцеальная проблемма модет быть (пока что пофиг)
                    .map(dataBuffer -> {
                        ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                        //DataBufferUtils.release(dataBuffer);
                        return byteBuffer;
                    });

            AsyncRequestBody requestBody = AsyncRequestBody.fromPublisher(byteBufferFlux);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(filePart.headers().getContentType() != null
                            ? filePart.headers().getContentType().toString()
                            : "application/octet-stream")
                    .contentLength(length)
                    .build();

            return Mono.fromFuture(() -> s3AsyncClient.putObject(request, requestBody))
                    .flatMap(resp -> {
                            //Создание записи в бд
                            return currentUserUtil.getCurrentUserId()
                                    .flatMap(userId -> {
                                        Media media = Media.builder().key(key).ownerId(userId).build();
                                        return mediaService.save(media).map(m -> {
                                            return ResponseEntity.ok(Map.of(
                                            "id", m.getId().toString(),
                                            "filename", filePart.filename(),
                                            "key", key,
                                            "status", "success"
                                            ));

                                        });

                                    });
                        }
                    )
                    .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().body(Map.of(
                            "filename", filePart.filename(),
                            "key", key,
                            "status", "error",
                            "message", e.getMessage()
                    ))));
        });
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
