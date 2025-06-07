package ru.kors.chatsservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final RestClient restClient;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    /**
     * Обновляет в Keycloak атрибут user_id, username и email у пользователя с заданным keycloakId,
     * используя данные из JWT (без дополнительного GET-запроса).
     *
     * @param keycloakId ID пользователя в Keycloak
     * @param userId     значение, которое надо записать в атрибут user_id
     * @param jwt        JWT с данными пользователя (username, email)
     */
    public void updateUserAttributesInKeycloak(String keycloakId, Long userId, Jwt jwt) {
        try {
            // Получаем токен администратора для доступа к Keycloak Admin API
            String adminAccessToken = getAdminAccessToken();

            // Извлекаем необходимые поля из JWT
            String username = jwt.getClaimAsString("preferred_username");
            String email = jwt.getClaimAsString("email");

            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("JWT не содержит preferred_username");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("JWT не содержит email");
            }

            // Формируем полный payload с полями username, email и атрибутом user_id
            Map<String, Object> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("email", email);
            payload.put("enabled", true); // Обычно поле обязательно, чтобы не выключить пользователя

            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("user_id", List.of(String.valueOf(userId)));
            payload.put("attributes", attributes);

            // Обновляем пользователя
            updateUser(keycloakId, adminAccessToken, payload);

            log.info("Пользователь Keycloak с ID={} успешно обновлён", keycloakId);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя в Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка обновления пользователя в Keycloak", e);
        }
    }

    private String getAdminAccessToken() {
        String tokenEndpoint = keycloakUrl + "/realms/master/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", adminClientId);
        params.add("username", adminUsername);
        params.add("password", adminPassword);
        params.add("grant_type", "password");

        Map<String, Object> response = restClient.post()
                .uri(tokenEndpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("В ответе отсутствует access_token");
        }

        return (String) response.get("access_token");
    }

    private void updateUser(String keycloakId, String accessToken, Map<String, Object> payload) {
        String userEndpoint = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakId;

        restClient.put()
                .uri(userEndpoint)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
