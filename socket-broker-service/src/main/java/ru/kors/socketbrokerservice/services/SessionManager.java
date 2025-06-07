package ru.kors.socketbrokerservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.kors.socketbrokerservice.api.UsersRestApi;
import ru.kors.socketbrokerservice.models.UserSession;
import ru.kors.socketbrokerservice.models.entity.ChatEvent;
import ru.kors.socketbrokerservice.models.entity.Message;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
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

    private static final Duration TTL = Duration.ofHours(48);

    private final ObjectMapper objectMapper;

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


    // serializers reused in pipelines
    private final RedisSerializer<String> STRING_SER = new StringRedisSerializer();
    private RedisSerializer<Long>   LONG_SER; //Инициализируется в PostConstruct, ибо longRedisTemplate будет null во время инициализации

    @PostConstruct
    public void init() {
        LONG_SER = (RedisSerializer<Long>) longRedisTemplate.getValueSerializer();
    }

    public void registerSession(WebSocketSession session) {
        log.info("Registering session {}", session.getId());
        Long userId = getUserId(session);

        if (userId == null) {
            closeBadSession(session);
            return;
        }

        // 1) register in‑memory
        UserSession userSession = new UserSession(userId, session);
        sessionsById.put(session.getId(), userSession);
        sessionsByUser.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(userSession);

        // 2) Redis‑keys
        String dataKey = "u:" + userId;
        String fetchedKey = dataKey + ":f";


        log.info("1");
        Set<Long> chatIds = new HashSet<>();; // Список чатов, на которые подписан пользователь
        boolean alreadyFetched = Boolean.TRUE.equals(stringRedisTemplate.hasKey(fetchedKey));
        log.info("2");

        if (!alreadyFetched) {
            chatIds = usersRestApi.getUserChatsIds(userId);
            log.debug("Fetched {} chats for user {}", chatIds.size(), userId);
            log.info("3");
            initializeUserChatsInRedis(dataKey, fetchedKey, chatIds);
            log.info("4");
        }
        else {
            log.info("5");
            reloadUserChatsInRedis(dataKey, fetchedKey, chatIds);
        }
        log.info("6");
        //

        log.info("user {} chats: {}", userId, chatIds);

        // Добавляем подписку на все чаты, на которые подписан пользователь

        subscribeSessionToSubscriptions(userSession, chatIds);

        log.debug("Registered session {} for user {}", session.getId(), userId);
    }


    private void closeBadSession(WebSocketSession session) {
        log.warn("Не удалось получить userId из токена для сессии {}", session.getId());
        try {
            session.close(CloseStatus.BAD_DATA);
        }
        catch (Exception e) {
            log.error("Ошибка при закрытии сессии {}: {}", session.getId(), e.getMessage());
        }
    }

    private void initializeUserChatsInRedis(String dataKey, String fetchedKey, Set<Long> chatIds) {
        longRedisTemplate.executePipelined((RedisCallback<Object>) conn -> {
            byte[] dataBytes    = STRING_SER.serialize(dataKey);
            byte[] fetchedBytes = STRING_SER.serialize(fetchedKey);

            if (!chatIds.isEmpty()) {
                for (Long id : chatIds) {
                    conn.sAdd(dataBytes, LONG_SER.serialize(id));
                }
                conn.expire(dataBytes, TTL.getSeconds());
            }
            conn.set(fetchedBytes, STRING_SER.serialize("1"));
            conn.expire(fetchedBytes, TTL.getSeconds());

            // Возвращаемое значение не используется, поэтому возвращаем null
            return null;
        });
    }

    @SuppressWarnings("unchecked") // отключает предупреждения о приведении типов
    private void reloadUserChatsInRedis(String dataKey, String fetchedKey, Set<Long> chatIds) {
        //TODO
        List<Object> replies = longRedisTemplate.executePipelined((RedisCallback<Object>) conn -> {
            byte[] dataBytes    = STRING_SER.serialize(dataKey);
            byte[] fetchedBytes = STRING_SER.serialize(fetchedKey);

            // SMEMBERS
            conn.sMembers(dataBytes);
            // EXPIRE dataKey
            conn.expire(dataBytes, TTL.getSeconds());
            // EXPIRE fetchedKey
            conn.expire(fetchedBytes, TTL.getSeconds());

            return null;
        });
        // replies.get(0) — результат SMEMBERS
        if (!replies.isEmpty()) {
            chatIds.clear();
            chatIds.addAll((Set<Long>) replies.get(0));
        }
    }

    private void subscribeSessionToSubscriptions(UserSession userSession, Set<Long> chatIds) {
        userSession.getSubscriptions().addAll(chatIds.stream()
                .map(chatId -> "c:" + chatId.toString())
                .collect(Collectors.toSet()));
        for (Long chatId : chatIds) {
            subscribedSessions.computeIfAbsent("c:" + chatId, k -> ConcurrentHashMap.newKeySet())
                    .add(userSession);
        }
    }




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


    public void localSubscribe(String subscriptionKey, UserSession userSession) {
        if (userSession == null) {
            log.warn("Session {} not found for subscription {}", userSession.getSession().getId(), subscriptionKey);
            return;
        }
        if (userSession.getSubscriptions().contains(subscriptionKey.substring(1))) {
            userSession.getSubscriptions().add(subscriptionKey); //extended
        }
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
    public void unLocalSubscribe(String subscriptionKey, UserSession userSession) {
        if (userSession == null) return;

        userSession.getSubscriptions().remove(subscriptionKey);

        Set<UserSession> subs = subscribedSessions.get(subscriptionKey);
        if (subs != null) {
            subs.remove(userSession);
            log.debug("Session {} for user {} unsubscribed from {}",
                    userSession.getSession().getId(), userSession.getUserId(), subscriptionKey);
        } else {
            log.warn("Tried to unsubscribe from non-existent key: {}", subscriptionKey);
        }
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
        String query = session.getUri().getQuery(); // "token=abc123"
        String token = null;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals("token")) {
                    token = pair[1];
                    break;
                }
            }
        }
        log.info("Token from session {}: {}", session.getId(), token);
        return token;
    }

    private Long getUserId(WebSocketSession session) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(getToken(session));
            return (Long) signedJWT.getJWTClaimsSet().getClaim("user_id");
        } catch (ParseException e) {
            throw new RuntimeException("Ошибка при разборе токена", e);
        }
    }

    public void sendMessage(Message message) {
        var users = subscribedSessions.get("c:" + message.getChatId());
        log.info("sessions in chats: {}", subscribedSessions.get("c:" + message.getChatId()));
        if (users == null || users.isEmpty()) {
            log.warn("No subscribers for chat {}", message.getChatId());
            return;
        }
        log.info("user size: {}", users.size());
        for (UserSession userSession : users) {
            WebSocketSession session = userSession.getSession();
            if (session.isOpen()) {
                log.info("sending message to {}", userSession.getUserId());
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                } catch (IOException e) {
                    log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
                }
            }
        }
    }

    public void sendChatEvent(ChatEvent event) {
        var users = subscribedSessions.get("c:" + event.getChatId());
        if (users == null || users.isEmpty()) {
            log.warn("No subscribers for chat {}", event.getChatId());
            return;
        }
        log.info("user size: {}", users.size());
        for (UserSession userSession : users) {
            WebSocketSession session = userSession.getSession();
            if (session.isOpen()) {
                log.info("sending event to {}", userSession.getUserId());
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
                } catch (IOException e) {
                    log.error("Error sending event to session {}: {}", session.getId(), e.getMessage());
                }
            }
        }
    }

    public void globalSubscribe(Long userId, String subscriptionKey) {
        String dataKey = "u:" + userId;

        longRedisTemplate.opsForSet().add(dataKey, Long.valueOf(subscriptionKey.split(":")[1]));

        if (!sessionsByUser.containsKey(userId)) {
            log.warn("No sessions found for user {}", userId);
            return;
        }

        for (UserSession userSession : sessionsByUser.get(userId)) {
            userSession.getSubscriptions().add(subscriptionKey);
            subscribedSessions.computeIfAbsent(subscriptionKey, k -> ConcurrentHashMap.newKeySet())
                    .add(userSession);
        }
    }

}
