package ru.kors.socketbrokerservice.config;

import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.kors.socketbrokerservice.api.UsersRestApi;
import ru.kors.socketbrokerservice.models.UserSession;
import ru.kors.socketbrokerservice.services.SessionManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
// Обработчик текстовых сообщений. Обработка бинарных в BinaryWebSocketHandler (или чет такое)
public class WebSocketHandler extends TextWebSocketHandler {
    private final StandardServletMultipartResolver standardServletMultipartResolver;
    @Value("${server.id}")
    private String serverId;

    private final WebSocketMessageHandler webSocketMessageHandler;
    private final SessionManager sessionManager;

    //ПРИ РЕГИСТРАЦИИ СЕССИ НЕОБХОДИМО ПЕРЕДАВАТЬ JWT В АТРИБУТАХ
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Установлено соединение с клиентом: {}", session.getId());
        sessionManager.registerSession(session);
    }

    @Override
    public void handlePongMessage(WebSocketSession session, PongMessage message) {
        UserSession userSession = sessionManager.getSessionById(session.getId());
        if (userSession != null) {
            userSession.updateLastPongTime();
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        // Обработка бинарных сообщений
        log.info("Получено бинарное сообщение от сессии {}: {}", session.getId(), message.getPayload());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            webSocketMessageHandler.handleMessage(session, message);
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения от WebSocket-клиента", e);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ioException) {
                log.error("Ошибка при закрытии WebSocket-сессии", ioException);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("close status session {}: {}", session.getId(), status);
        sessionManager.unregisterSession(session.getId());
    }
}
