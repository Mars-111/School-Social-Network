package ru.kors.socketbrokerservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import ru.kors.socketbrokerservice.controllers.payload.ChatMessagePayload;
import ru.kors.socketbrokerservice.controllers.payload.ChatPersonalMessagePayload;
import ru.kors.socketbrokerservice.exceptions.UserNotAuthorizedException;
import ru.kors.socketbrokerservice.services.JwtService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class EasyController {

    private final JwtService jwtService;


    @MessageMapping("/all")
    @SendTo("/topic/all")
    public ChatMessagePayload allSendMessage(@Header("token") String token, ChatMessagePayload messagePayload) throws Exception {
        log.info("Token: " + token);
        var user = jwtService.validateToken(token).get("preferred_username", String.class);//.getSubject();

        if (user == null) {
            log.warn("Controller allSendMessage: socket get message by user is null");
            throw new UserNotAuthorizedException("User  is not authenticated");
        }

        messagePayload.setSender(user);
        return messagePayload;
    }



}
