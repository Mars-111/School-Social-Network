package ru.listener.keycloakeventlistener.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
public class CustomEventListenerProvider implements EventListenerProvider {

    @Override
    public void onEvent(Event event) {
        if ("REGISTER".equals(event.getType().toString())) {
            // Создаем синхронный HTTP клиент
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = null;
            HttpResponse<String> response = null;

            // Создаем Map для данных запроса
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("tag", event.getDetails().get("tag"));
            requestBody.put("keycloak_id", event.getUserId());

            // Используем ObjectMapper для преобразования Map в JSON строку
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = null;
            try {
                jsonBody = objectMapper.writeValueAsString(requestBody);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // Отправляем синхронный POST запрос с JSON телом
                request = HttpRequest.newBuilder()
                        .uri(new URI("http://host.docker.internal:8082/internal/api/users"))
                        .header("Content-Type", "application/json") // Устанавливаем Content-Type как application/json
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // Добавляем тело запроса
                        .build();

                // Синхронно отправляем запрос
                log.info("Sending request to create user with keycloak_id: " + event.getUserId() + " and tag: " + event.getDetails().get("tag"));
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }

            // Логируем ответ
            if (response != null) {
                System.out.println("Response: " + response.body());
            }
        }
    }


    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        // Your custom logic for admin events here
    }

    @Override
    public void close() {
        // Cleanup resources if needed
    }
}