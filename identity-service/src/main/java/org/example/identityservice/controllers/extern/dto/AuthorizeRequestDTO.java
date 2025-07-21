package org.example.identityservice.controllers.extern.dto;

public record AuthorizeRequestDTO(
    String username,
    String password
) {
}
