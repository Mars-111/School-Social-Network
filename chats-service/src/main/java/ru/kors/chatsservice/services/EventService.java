package ru.kors.chatsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kors.chatsservice.models.entity.Event;
import ru.kors.chatsservice.repositories.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    public Event createEvent(Event event) {
        event.setId(null);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public List<Event> findAllByChatId(Long chatId) {
        return eventRepository.findAllByChat_Id(chatId);
    }

    public Event getEventByChatTag(String chatTag) {
        return eventRepository.findByChat_Tag(chatTag).orElse(null);
    }
}