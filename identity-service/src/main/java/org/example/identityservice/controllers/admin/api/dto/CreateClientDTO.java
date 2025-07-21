package org.example.identityservice.controllers.admin.api.dto;


import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateClientDTO(
        @NotBlank(message = "Name is required")
        String id,
        String description,
        String secret,
        List<String> redirectUris
) {
}
