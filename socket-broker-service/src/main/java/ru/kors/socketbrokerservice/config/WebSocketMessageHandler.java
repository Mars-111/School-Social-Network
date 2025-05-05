package ru.kors.socketbrokerservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.kors.socketbrokerservice.models.UserSession;
import ru.kors.socketbrokerservice.services.SessionManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageHandler {

    private final SessionManager sessionManager;

    public void handleMessage(WebSocketSession session, TextMessage message) {
        String messagePayload = message.getPayload();
        if (messagePayload.isEmpty()) return;
        UserSession userSession = sessionManager.getSessionById(session.getId());

        switch (messagePayload.charAt(0)) {
            case 'G': //Global subscription
                sessionManager.localSubscribe(messagePayload, userSession);
                log.info("User {} subscribed to {}", userSession.getUserId(), messagePayload);
                break;
            case 'U': //К примеру UG12 -> отписываемся от G12
                sessionManager.unLocalSubscribe(messagePayload.substring(1), userSession);
                log.info("User {} unsubscribed from {}", userSession.getUserId(), messagePayload.substring(1));
                break; //UnSub
            case 'T': //Status online
                switch (messagePayload.charAt(1)) {
                    case '0': //Status
                        userSession.setOnline(false);
                        log.info("User {} is offline", userSession.getUserId());
                        break; //Status
                    case '1': //Status
                        userSession.setOnline(true);
                        log.info("User {} is online", userSession.getUserId());
                        break; //Status
                    default:
                        log.warn("Unknown message type: {}", messagePayload);
                }
                break; //Status
            default:
                log.warn("Unknown message type: {}", messagePayload);
        }
    }

}
