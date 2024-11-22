package ru.kors.webclient.client;


import org.springframework.http.ResponseEntity;
import ru.kors.webclient.client.payload.CreateUserPayload;

import java.util.List;
import java.util.Optional;

public interface UserRestClient {
    ResponseEntity<String> createUser(CreateUserPayload userPayload);

}
