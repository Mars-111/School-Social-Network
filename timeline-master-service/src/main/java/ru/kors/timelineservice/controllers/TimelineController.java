package ru.kors.timelineservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kors.timelineservice.services.TimelineService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/")
public class TimelineController {
    private final TimelineService timelineService;

    @GetMapping("/{chatId}/next")
    public int getNextOrderId(@PathVariable Long chatId) {
        return timelineService.getNextTimelineId(chatId);
    }
}