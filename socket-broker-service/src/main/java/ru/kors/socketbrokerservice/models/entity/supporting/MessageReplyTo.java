package ru.kors.socketbrokerservice.models.entity.supporting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageReplyTo {
    private Long id;

    private Integer flags = 0;

    private String type;

    @JsonProperty("sender_id")
    private Long senderId;

    private String content;

    @JsonProperty("media")
    private List<Long> mediaList;

    @JsonProperty("reply_to_id")
    private Long replyToId;

    @JsonProperty("forwarded_id")
    private Long forwardedFromId;

    private Instant timestamp;
}
