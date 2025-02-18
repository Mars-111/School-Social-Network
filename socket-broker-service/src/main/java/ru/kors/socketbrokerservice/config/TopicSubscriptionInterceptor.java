package ru.kors.socketbrokerservice.config;


import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import ru.kors.socketbrokerservice.services.JwtService;
import ru.kors.socketbrokerservice.services.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TopicSubscriptionInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("token");
            String destination = accessor.getDestination();

            if (token == null || destination == null) {
                log.warn("Missing token or destination in subscription request");
                return null;
            }

            String user = jwtService.validateToken(token).getSubject();
            if (user == null) {
                log.warn("User {} is not authorized to subscribe to {}", user, destination);
                return null;
            }

            //TODO: проверить может ли пользователь писать туда или нет
            if (destination.startsWith("/chat/")) {
                if (userService.isUserInChat(jwtService.getUserFromToken(token).getId(),
                        Long.parseLong(destination.split("/")[2]))) {
                    return message;
                }
                else {
                    return null;
                }
            }
            else if (destination.startsWith("/user/")) {
                if (jwtService.validateToken(token).getSubject().equals(destination.split("/")[2])) {
                    return message;
                }
                else {
                    return null;
                }
            }

        }

        return message;
    }
}