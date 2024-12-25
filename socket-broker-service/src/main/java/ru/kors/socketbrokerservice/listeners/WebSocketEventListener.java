package ru.kors.socketbrokerservice.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.kors.socketbrokerservice.controllers.payload.ChatMessagePayload;
import ru.kors.socketbrokerservice.models.enums.MessageType;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {

        //log.info("New user: {}", event.getMessage().getHeaders());
//        String username = Objects.requireNonNull(event.getUser()).getName();
//        var chatMessage = ChatMessagePayload.builder()
//                    .type(MessageType.JOIN)
//                    .content(".")
//                    .sender(username)
//                    .build();
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        //log.info("User disconnected: {}", event.getUser().getName());

        log.info("User disconnected: {}", event.getMessage().getHeaders().get("token"));
    }

}

//
//package com.alibou.websocket.config;
//
//import com.alibou.websocket.chat.ChatMessage;
//import com.alibou.websocket.chat.MessageType;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionConnectEvent;
//import org.springframework.web.socket.messaging.SessionConnectedEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.Objects;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class WebSocketEventListener {
//
//    private final SimpMessageSendingOperations messagingTemplate;
//
//    //TODO - Удалить коннектед и спринг (не будет работать это приложение прост)
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //OR event.getUser().getName()
//        if (auth != null && auth.isAuthenticated()) {
//            String username = auth.getName(); // Имя пользователя
//            log.info("Успешно подключен пользователь: {}", username);
//            var chatMessage = ChatMessage.builder()
//                    .type(MessageType.JOIN)
//                    .sender(username)
//                    .build();
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        } else {
//            log.warn("Пользователь не аутентифицирован");
//        }
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//        if (username != null) {
//            log.info("user disconnected: {}", username);
//            var chatMessage = ChatMessage.builder()
//                    .type(MessageType.LEAVE)
//                    .sender(username)
//                    .build();
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
//        }
//    }
//
//}