//package ru.kors.storemediaservice.services;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class KafkaProducerService {
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public void sendFileStatusNotification(Long fileId, String status) {
//        kafkaTemplate.send("file-status", "{\"id\":" + fileId + ",\"status\": " + status + "}");
//    }
//
//}
