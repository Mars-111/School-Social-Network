package ru.kors.socketbrokerservice.api.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignChatsPayload {
    private Long userId;
    private List<Long> chatIds;

    public AssignChatsPayload(Long userId, List<Long> chatIds) {
        this.userId = userId;
        this.chatIds = chatIds;
    }
}