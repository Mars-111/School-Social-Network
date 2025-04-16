package ru.kors.socketbrokerservice.services;

import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import ru.kors.socketbrokerservice.api.UsersRestApi;
import ru.kors.socketbrokerservice.models.UserSession;

import java.text.ParseException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SessionManager {
    @Value("${server.id}")
    private String serverId;

    //Rest
    private final UsersRestApi usersRestApi;

    //Redis
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    // Индекс сессий по userId: позволяет быстро получить все сессии для конкретного пользователя.
    private final ConcurrentMap<Long, Set<UserSession>> sessionsByUser = new ConcurrentHashMap<>();

    // Индекс сессий по sessionId: ключ – sessionId, значение – набор UserSession (обычно один объект, но оставляем Set для гибкости).
    private final ConcurrentMap<String, UserSession> sessionsById = new ConcurrentHashMap<>();

    // Индекс подписок: ключ – подписка (например, "chat:17"), значение – набор UserSession,
    // подписанных на данное событие.
    private final ConcurrentMap<String, Set<UserSession>> subscribedSessions = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session) {
        Long userId = getUserId(session);

        // Создаем новый объект UserSession с пустым набором подписок по умолчанию.
        UserSession userSession = new UserSession(userId, session);

        // Используем userId из userSession, так как это дублирование — оно гарантирует согласованность
        sessionsById.put(session.getId(), userSession);
        sessionsByUser.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(userSession);


        Set<Long> chatIds; // Список чатов, на которые подписан пользователь

        //Redis
        // Добавляем сессию в редис
        //stringRedisTemplate.opsForSet().add("sessions:" + userSession.userId(), session.getId() + "@" + serverId);
        // Добавляем пользователя в подписки его чатов
        Boolean exists = longRedisTemplate.hasKey("user:" + userId);
        if (!exists) {
            chatIds = usersRestApi.getUserChatsIds(userId);
            longRedisTemplate.opsForSet().add("user:" + userId, chatIds.toArray(new Long[0]));
        }
        else {
            chatIds = longRedisTemplate.opsForSet().members("user:" + userId);
        }
        longRedisTemplate.expire("user:" + userId, Duration.ofHours(48));
        //

        // Добавляем подписку на все чаты, на которые подписан пользователь
        userSession.getSubscriptions().addAll(chatIds.stream()
                .map(chatId -> "c:" + chatId.toString())
                .collect(Collectors.toSet()));
        for (Long chatId : chatIds) {
            subscribedSessions.computeIfAbsent("c:" + chatId, k -> ConcurrentHashMap.newKeySet())
                    .add(userSession);
        }


        log.debug("Registered session {} for user {}", session.getId(), userId);
    }



    /**
     * Удаляет сессию для указанного пользователя по sessionId.
     * Удаляет сессию из всех структур: userSessions, sessionsById и subscribedSessions.
     *
     * @param sessionId идентификатор сессии, которая закрывается
     */
    public void unregisterSession(String sessionId) {
        // Удаляем из userSessions.
        UserSession userSession = sessionsById.get(sessionId);
        if (userSession == null) {
            log.warn("Session {} not found for unregister", sessionId);
            return;
        }

        sessionsById.remove(sessionId);
        sessionsByUser.computeIfPresent(userSession.getUserId(), (k, v) -> {
            v.remove(userSession);
            return v.isEmpty() ? null : v;
        });
        // Удаляем из подписок.
        for (String subscriptionKey : userSession.getSubscriptions()) {
            Set<UserSession> subSet = subscribedSessions.get(subscriptionKey);
            if (subSet != null) {
                subSet.remove(userSession);
                if (subSet.isEmpty()) {
                    subscribedSessions.remove(subscriptionKey);
                }
            }
        }

        log.debug("Unregistered session {} for user {}", sessionId, userSession.getUserId());
    }

    /**
     * Подписывает сессию на заданный подписочный ключ.
     * Например, для чата с идентификатором 17 можно использовать ключ "c:17".
     *
     * @param subscriptionKey подписочный ключ
     * @param userSession     сессия пользователя
     */
    public void subscribe(String subscriptionKey, UserSession userSession) {
        if (userSession == null) {
            log.warn("Session {} not found for subscription {}", userSession.getSession().getId(), subscriptionKey);
            return;
        }
        // Добавляем подписку для каждой найденной сессии.
        userSession.getSubscriptions().add(subscriptionKey);
        // Добавляем сессию в глобальный индекс подписок.
        subscribedSessions.computeIfAbsent(subscriptionKey, k -> ConcurrentHashMap.newKeySet())
                .add(userSession);
    }

    /**
     * Отписывает сессию от указанного подписочного ключа.
     *
     * @param subscriptionKey подписочный ключ
     * @param userSession     сессия пользователя
     */
    public void unsubscribe(String subscriptionKey, UserSession userSession) {
        if (userSession == null) return;
        userSession.getSubscriptions().remove(subscriptionKey);

        // Обновляем глобальный индекс подписок.
        subscribedSessions.get(subscriptionKey).remove(userSession);
        log.debug("Session {} for user {} unsubscribed from {}", userSession.getSession().getId(),userSession.getUserId(), subscriptionKey);
    }

    /**
     * Возвращает все сессии для указанного пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return множество UserSession
     */
    public Set<UserSession> getSessionsByUserId(Long userId) {
        return sessionsByUser.getOrDefault(userId, ConcurrentHashMap.newKeySet());
    }

    /**
     * Возвращает сессию по sessionId.
     *
     * @param sessionId идентификатор сессии.
     * @return UserSession
     */
    public UserSession getSessionById(String sessionId) {
        return sessionsById.getOrDefault(sessionId, null);
    }

    /**
     * Возвращает всех подписчиков по заданному подписочному ключу.
     *
     * @param subscriptionKey подписочный ключ (например, "chat:17")
     * @return множество UserSession
     */
    public Set<UserSession> getSubscribers(String subscriptionKey) {
        return subscribedSessions.getOrDefault(subscriptionKey, ConcurrentHashMap.newKeySet());
    }

    public Set<UserSession> getSessions() {
        return new HashSet<>(sessionsById.values());
    }


    private String getToken(WebSocketSession session) {
        return session.getAttributes().get("token").toString();
    }

    private Long getUserId(WebSocketSession session) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(getToken(session));
            return (Long) signedJWT.getJWTClaimsSet().getClaim("user_id");
        } catch (ParseException e) {
            throw new RuntimeException("Ошибка при разборе токена", e);
        }
    }

    public void updateLastActive(String sessionId) {
        UserSession userSession = sessionsById.get(sessionId);
        if (userSession != null) {
            userSession.updateLastPongTime();
        }
    }
}
