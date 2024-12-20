package ru.kors.socketbrokerservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class EasyController {

    @MessageMapping("/all")
    @SendTo("/topic/all")
    public String allSendMessage(String message) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info(auth.getName() + ": " + message);
        return auth.getName() + ": " + message;
    }

}
