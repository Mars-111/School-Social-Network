package ru.kors.storemediaservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Config config;

    public void upload(String key, MultipartFile file) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(config.getBucket())
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
    }

    public URL generateDownloadUrl(String key, Duration expiresIn) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(config.getBucket())
                .key(key)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(
                b -> b.getObjectRequest(getObjectRequest)
                        .signatureDuration(expiresIn)
        );

        return presigned.url();
    }

    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(config.getBucket())
                .key(key)
                .build());
    }
}

