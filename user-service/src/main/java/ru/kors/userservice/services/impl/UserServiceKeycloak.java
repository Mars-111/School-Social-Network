package ru.kors.userservice.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kors.userservice.controllers.payload.CreateUserPayload;
import ru.kors.userservice.services.KeycloakService;
import ru.kors.userservice.services.UserService;

import java.util.Objects;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceKeycloak implements UserService {
    private final RestClient restClient;
    private final KeycloakService keycloakService;

    public ResponseEntity<String> createUser(CreateUserPayload user)  {

        if (Objects.requireNonNull(this.findUsers(user.username(), true).getBody()).length() > 3) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user with the same name already exists.");
        }
        else if (Objects.requireNonNull(this.findUsersByEmail(user.email(), true).getBody()).length() > 3) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user with the same email already exists.");
        }

        ResponseEntity<String> response = restClient
                .post()
                .uri("http://localhost:8082/admin/realms/school-social-network/users")
                .headers(headers -> {
                    try {
                        headers.add("Authorization", "Bearer " + keycloakService.getAccessToken());
                    } catch (JsonProcessingException e) {
                        log.error("error access token!");
                        throw new RuntimeException(e);
                    }
                    headers.add("Content-Type", "application/json");
                })
                .body(user.toBody())
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            return ResponseEntity.status(response.getStatusCode()).body("Failed to create user: " + response.getBody());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    public ResponseEntity<String> findUsers(String username, Boolean exact) {
        String uri = UriComponentsBuilder.fromHttpUrl("http://localhost:8082/admin/realms/school-social-network/users")
                .queryParam("username", username)
                .queryParam("exact", exact)
                .toUriString();
        log.info(uri);
        return restClient
                .get()
                .uri(uri)
                .headers(headers -> {
                    try {
                        headers.add("Authorization", "Bearer " + keycloakService.getAccessToken());
                    } catch (JsonProcessingException e) {
                        log.error("error access token!");
                        throw new RuntimeException(e);
                    }
                })
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> findUsersByEmail(String email, Boolean exact) {
        String uri = UriComponentsBuilder.fromHttpUrl("http://localhost:8082/admin/realms/school-social-network/users")
                .queryParam("email", email)
                .queryParam("exact", exact)
                .toUriString();

        return restClient
                .get()
                .uri(uri)
                .headers(headers -> {
                    try {
                        headers.add("Authorization", "Bearer " + keycloakService.getAccessToken());
                    } catch (JsonProcessingException e) {
                        log.error("error access token!");
                        throw new RuntimeException(e);
                    }
                })
                .retrieve()
                .toEntity(String.class);
    }
}
