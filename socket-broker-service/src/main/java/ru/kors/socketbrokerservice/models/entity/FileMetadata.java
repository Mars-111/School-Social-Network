package ru.kors.socketbrokerservice.models.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    @JsonProperty("file_id")
    private Long fileId;

    @JsonProperty("message_id")
    private Long messageId;

    private String extension;
    private String filename;
    private Integer size;
    private Integer height;
    private Integer width;
}

