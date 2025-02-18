package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.models.Event;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventsRestApi {

    private final RestClient restClient;
    private final String baseUrl = "http://localhost:8082/internal/api/events";

    private static final ParameterizedTypeReference<List<Event>> EVENT_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {};

    public List<Event> findAll() {
        try {
            return restClient.get()
                    .uri(baseUrl)
                    .retrieve()
                    .body(EVENT_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error fetching all events", e);
            return List.of();
        }
    }

    public Optional<Event> findById(Long id) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/" + id)
                    .retrieve()
                    .body(Event.class));
        } catch (RestClientException e) {
            log.error("Error fetching event by id: {}", id, e);
            return Optional.empty();
        }
    }

    public List<Event> findAllByChatId(Long chatId) {
        try {
            return restClient.get()
                    .uri(baseUrl + "/chat/" + chatId)
                    .retrieve()
                    .body(EVENT_LIST_TYPE_REFERENCE);
        } catch (RestClientException e) {
            log.error("Error fetching events by chatId: {}", chatId, e);
            return List.of();
        }
    }

    public Optional<Event> createEvent(Event event) {
        try {
            return Optional.ofNullable(restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(event)
                    .retrieve()
                    .body(Event.class));
        } catch (RestClientException e) {
            log.error("Error creating event", e);
            return Optional.empty();
        }
    }

    public void deleteEvent(Long id) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + id)
                    .retrieve();
        } catch (RestClientException e) {
            log.error("Error deleting event with id: {}", id, e);
        }
    }
}
