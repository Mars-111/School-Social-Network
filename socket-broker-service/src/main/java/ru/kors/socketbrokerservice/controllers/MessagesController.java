package ru.kors.socketbrokerservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.kors.socketbrokerservice.controllers.payload.ChatMessagePayload;
import ru.kors.socketbrokerservice.exceptions.UserNotAuthorizedException;
import ru.kors.socketbrokerservice.services.JwtService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {
    private final JwtService jwtService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send-message")
    public void sendMessage(@Header("token") String token, ChatMessagePayload messagePayload) throws Exception {
        log.info("Token: " + token);
        var user = jwtService.validateToken(token).get("preferred_username", String.class);//.getSubject();

        if (user == null) {
            log.warn("Controller allSendMessage: socket get message by user is null");
            throw new UserNotAuthorizedException("User  is not authenticated");
        }

        messagePayload.setSender(user);
        String destination = "/topic/" + messagePayload.getDestination(); // Assuming `getDestination` method exists in `ChatMessagePayload`
        //Проверка: может ли юзер отправлять сообщения в этот канал
        messagingTemplate.convertAndSend(destination, messagePayload);
    }
}
