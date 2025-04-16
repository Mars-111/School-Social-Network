package ru.kors.socketbrokerservice.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String tag;
    private String keycloakId;
}
