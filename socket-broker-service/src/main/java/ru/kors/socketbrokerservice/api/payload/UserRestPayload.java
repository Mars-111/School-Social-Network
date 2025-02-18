package ru.kors.socketbrokerservice.api.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRestPayload {
    //private Long id;
    private String keycloakId;
}