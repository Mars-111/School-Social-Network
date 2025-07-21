package org.example.identityservice.controllers.extern.dto;

public record CreateUserDTO(
    String username,
    String password,
    String email
) {
}
