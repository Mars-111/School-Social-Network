package ru.kors.socketbrokerservice.models.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import ru.kors.socketbrokerservice.models.entity.supporting.MessageForwardedFrom;
import ru.kors.socketbrokerservice.models.entity.supporting.MessageReplyTo;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private Long id;

    @JsonProperty("timeline_id")
    private Integer timelineId;

    private Integer flags = 0;

    private String type;

    @JsonProperty("chat_id")
    private Long chatId;

    @JsonProperty("sender_id")
    private Long senderId;

    private String content;

    @JsonProperty("files")
    private List<FileMetadata> fileMetadata;

    @JsonProperty("reply_to")
    private MessageReplyTo replyTo;

    @JsonProperty("forwarded_from")

    private MessageForwardedFrom forwardedFrom;

    private Instant timestamp;
}