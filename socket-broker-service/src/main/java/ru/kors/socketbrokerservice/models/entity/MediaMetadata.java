package ru.kors.socketbrokerservice.models.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaMetadata {
    @JsonProperty("media_id")
    private Long mediaId;

    @JsonProperty("message_id")
    private Long messageId;

    private String extension;
    private String filename;
    private Integer size;
    private Integer height;
    private Integer width;
}

