package ru.kors.webclient.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import ru.kors.webclient.client.UserRestClient;
import ru.kors.webclient.client.payload.CreateUserPayload;

@Primary
@RequiredArgsConstructor
public class UserRestClientKeycloak implements UserRestClient {
    private final RestClient restClient;

    @Override
    public ResponseEntity<String> createUser(CreateUserPayload userPayload) {
        return restClient
                .post()
                .uri("/user")
                .body(userPayload)
                .retrieve()
                .toEntity(String.class);
    }
}
