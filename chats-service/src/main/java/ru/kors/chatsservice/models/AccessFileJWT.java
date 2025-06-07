package ru.kors.chatsservice.models;

public record AccessFileJWT(
        Long fileId,
        Long userId,
        String subject,
        Long size,
        String extension,
        String filename,
        FileMetadata metadata
) {
    public record FileMetadata(
            int width,
            int height
    ) {
    }
}
