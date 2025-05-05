package ru.kors.chatsservice.controllers.external.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import ru.kors.chatsservice.exceptions.NotAuthException;
import ru.kors.chatsservice.exceptions.NotFoundEntityException;
import ru.kors.chatsservice.models.entity.Chat;
import ru.kors.chatsservice.models.entity.User;
import ru.kors.chatsservice.services.UserService;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CurrentUserUtil {
    private final UserService userService;

    /**
     * Получаем текущего пользователя из SecurityContextHolder
     * ВНИМАНИЕ, ЭТО В РАЗЫ ЗАТРАТНЕЕ ЧЕМ getCurrentUserId! ТУТ МЫ ОТПРАЛЯЕМ ЗАПРОС К БД!
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        //TODO: снести этот метод и использовать getCurrentUserId
        // Получаем текущего пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Проверяем, авторизован ли пользователь
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthException("Unauthorized access");
        }
        String keycloakId = authentication.getName(); // subject из токена
        // Получаем пользователя из базы
        User user = userService.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new NotFoundEntityException("User not found");
        }
        return user;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthException("Unauthorized access");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            Object claim = jwt.getClaim("user_id");
            if (claim != null) {
                return Long.parseLong(claim.toString());
            }
        }

        throw new NotFoundEntityException("user_id not found in token");
    }


    public Boolean thisUserIsOwnerChat(Chat chat) {
        return chat.getOwner() == getCurrentUser();
    }
}
