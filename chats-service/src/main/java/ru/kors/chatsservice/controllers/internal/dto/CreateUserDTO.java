package ru.kors.chatsservice.controllers.internal.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateUserDTO(
        String tag,
        @JsonProperty("keycloak_id")
        String keycloakId
) {
}
