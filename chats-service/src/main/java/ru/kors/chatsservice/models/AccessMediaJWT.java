package ru.kors.chatsservice.models;

public record AccessMediaJWT (
        Long mediaId,
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
