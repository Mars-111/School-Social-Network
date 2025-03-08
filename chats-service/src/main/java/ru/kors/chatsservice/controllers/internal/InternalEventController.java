//package ru.kors.chatsservice.controllers.internal;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.kors.chatsservice.models.entity.Event;
//import ru.kors.chatsservice.services.EventService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/internal/api/events")
//@RequiredArgsConstructor
//public class InternalEventController {
//
//    private final EventService eventService;
//
//    @GetMapping
//    public List<Event> getAllEvents() {
//        return eventService.getAllEvents();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
//        Event event = eventService.getEventById(id);
//        if (event == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(event);
//    }
//
//    @GetMapping("/chat/{chatId}")
//    public ResponseEntity<List<Event>> getEventByChatId(@PathVariable Long chatId) {
//        List<Event> event = eventService.findAllByChatId(chatId);
//        if (event == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(event);
//    }
//
//    @PostMapping
//    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
//        Event createdEvent = eventService.createEvent(event);
//        return ResponseEntity.ok(createdEvent);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
//        eventService.deleteEvent(id);
//        return ResponseEntity.noContent().build();
//    }
//}