package ru.kors.socketbrokerservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.kors.socketbrokerservice.models.UserSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartbeatService {

    // SessionManager — Spring-синглтон, в котором хранятся актуальные сессии.
    private final SessionManager sessionManager;

    // Порог неактивности (в миллисекундах), например, 15 секунд.

    @Value("${websocket.heartbeat.inactivity-timeout}")
    private long inactivityTimeout;

    /**
     * Каждые 10 секунд отправляем ping и проверяем, получили ли мы pong.
     * Если сессия не отвечает в течение заданного порога, закрываем и удаляем её.
     */
    @Scheduled(fixedRate = 10000) // каждые 10 секунд
    public void sendHeartbeat() {
        long now = System.currentTimeMillis();

        Set<UserSession> sessions = sessionManager.getSessions(); //Уже снапшот, ибо return new HashSet<>(sessionsById.values());
        // Преобразуем в массив, чтобы избежать гонок с модификацией коллекции.
        for (UserSession userSession : sessions) {
            WebSocketSession wsSession = userSession.getSession();

            // Если сессия закрыта — удаляем её.
            if (!wsSession.isOpen()) {
                log.debug("Heartbeat: сессия {} закрыта. Удаляем.", wsSession.getId());
                sessionManager.unregisterSession(wsSession.getId());
                continue;
            }

            // Отправляем ping с payload (можно оставить пустым, если не нужен).
            try {
                wsSession.sendMessage(new PingMessage());
            } catch (IOException e) {
                log.warn("Heartbeat: не удалось отправить ping для сессии {} (userId: {}): {}",
                        wsSession.getId(), userSession.getUserId(), e.getMessage());
                closeAndUnregister(wsSession);
                continue;
            }

            // Если время с момента последнего pong превышает порог, считаем, что сессия неактивна
            if (now - userSession.getLastPongTime() > inactivityTimeout) {
                log.warn("Heartbeat: сессия {} (userId: {}) не отвечала более {} мс. Закрываем.",
                        wsSession.getId(), userSession.getUserId(), inactivityTimeout);
                closeAndUnregister(wsSession);
            }
        }
    }

    private void closeAndUnregister(WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException ex) {
            log.warn("Heartbeat: ошибка при закрытии сессии {}: {}", session.getId(), ex.getMessage());
        }
        sessionManager.unregisterSession(session.getId());
    }
}
