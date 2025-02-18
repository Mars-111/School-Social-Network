package ru.kors.chatsservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kors.chatsservice.services.KafkaProducerService;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaTest {
    private final KafkaProducerService kafkaProducerService;

    @GetMapping("/test")
    public String test() {
        kafkaProducerService.sendTest("test", "Test message");
        return "Kafka works!";
    }
}
