package ru.kors.socketbrokerservice.api.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String keycloakId;
}