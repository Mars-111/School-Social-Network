package ru.kors.socketbrokerservice.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Duration subscriptionTtl = Duration.ofHours(12);

    private String getUserSubscriptionsKey(Long userId) {
        return "user:" + userId + ":subscriptions";
    }

    private String getChatSubscribersKey(Long chatId) {
        return "chat:" + chatId + ":subscribers";
    }

    /**
     * Подписка пользователя на чат
     */
    public void subscribeToChat(Long userId, Long chatId) {
        redisTemplate.opsForSet().add(getUserSubscriptionsKey(userId), chatId.toString());
        redisTemplate.opsForSet().add(getChatSubscribersKey(chatId), userId.toString());

        redisTemplate.expire(getUserSubscriptionsKey(userId), subscriptionTtl);
        redisTemplate.expire(getChatSubscribersKey(chatId), subscriptionTtl);
    }

    /**
     * Получить все ID чатов, на которые подписан пользователь
     */
    public Set<String> getUserSubscriptions(Long userId) {
        return redisTemplate.opsForSet().members(getUserSubscriptionsKey(userId));
    }

    /**
     * Получить всех подписчиков чата
     */
    public Set<String> getChatSubscribers(Long chatId) {
        return redisTemplate.opsForSet().members(getChatSubscribersKey(chatId));
    }

    /**
     * Удалить подписку пользователя на чат
     */
    public void unsubscribeFromChat(Long userId, Long chatId) {
        redisTemplate.opsForSet().remove(getUserSubscriptionsKey(userId), chatId.toString());
        redisTemplate.opsForSet().remove(getChatSubscribersKey(chatId), userId.toString());
    }

    /**
     * Очистить все подписки пользователя (например, при логауте)
     */
    public void clearUserSubscriptions(Long userId) {
        redisTemplate.delete(getUserSubscriptionsKey(userId));
    }

    /**
     * Очистить всех подписчиков чата (например, при удалении чата)
     */
    public void clearChatSubscribers(Long chatId) {
        redisTemplate.delete(getChatSubscribersKey(chatId));
    }
}

