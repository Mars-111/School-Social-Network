package ru.kors.socketbrokerservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.kors.socketbrokerservice.api.payload.Event;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EventRestApi {
    private final RestClient restClient;
    private final String baseUrl = "http://localhost:8080/api/events";
    private static final ParameterizedTypeReference<List<Event>> EVENT_LIST_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {};

    public List<Event> getAllEvents() {
        return restClient.get()
                .uri(baseUrl)
                .retrieve()
                .body(EVENT_LIST_TYPE_REFERENCE);
    }

    public Optional<Event> getEventById(Long id) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(baseUrl + "/" + id)
                    .retrieve()
                    .body(Event.class));
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }

    public Event createEvent(Event event) {
        try {
            return restClient.post()
                    .uri(baseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(event)
                    .retrieve()
                    .body(Event.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public void deleteEvent(Long id) {
        try {
            restClient.delete()
                    .uri(baseUrl + "/" + id)
                    .retrieve();
        } catch (RestClientException e) {
            // Handle exception
        }
    }
}