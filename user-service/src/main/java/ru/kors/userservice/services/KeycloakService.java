package ru.kors.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final RestClient restClient;

    public String getAccessToken() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(
                restClient
                        .post()
                        .uri("http://localhost:8082/realms/school-social-network/protocol/openid-connect/token")
                        .headers(headers -> headers.add("Content-Type", "application/x-www-form-urlencoded"))
                        .body("grant_type=client_credentials&client_id=user-service&client_secret=uDHrhwFRnNm3lDMNYd1geQuO9sogNnXi")
                        .retrieve()
                        .body(String.class)
        );
        String token = jsonNode.get("access_token").asText();
        if (token.isEmpty()) {
            throw new InvalidBearerTokenException("access_token invalid");
        }
        return jsonNode.get("access_token").asText();
    }
}
