package ru.kors.chatsservice.models.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kors.chatsservice.models.AccessMediaJWT;
import ru.kors.chatsservice.models.entity.serializers.MediaMetadataSerializer;

@Entity
@Getter
@Setter
@Table(name = "media_meta")
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(using = MediaMetadataSerializer.class)
public class MediaMetadata {
    @Id
    @Column(name = "media_id", unique = true)
    private Long mediaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private Long size;

    private Integer height;

    private Integer width;


    public MediaMetadata(AccessMediaJWT jwt) {
        this.mediaId = jwt.mediaId();
        this.extension = jwt.extension();
        this.size = jwt.size();
        this.filename = jwt.filename();
        if (jwt.metadata() != null) {
            this.height = jwt.metadata().height();
            this.width = jwt.metadata().width();
        }
    }

}
