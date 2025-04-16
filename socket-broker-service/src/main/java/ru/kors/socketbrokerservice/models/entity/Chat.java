package ru.kors.socketbrokerservice.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Chat {
    private Long id;
    private Long ownerId;
    private String tag;
    private String name;
    private Boolean privateChat;
}
