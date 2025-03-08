package ru.kors.socketbrokerservice.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kors.socketbrokerservice.api.UsersRestApi;
import ru.kors.socketbrokerservice.api.payload.CreateUserPayload;
import ru.kors.socketbrokerservice.services.JwtService;

import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    private final UsersRestApi usersRestApi;
    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Читаем токен из заголовка или query-параметра
        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Убираем "Bearer "
        }

        if (token == null) {
            List<String> tokenList = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().get("token");
            token = (tokenList != null && !tokenList.isEmpty()) ? tokenList.get(0) : null;
        }

        log.info("Token: " + token);

        if (token == null) {
            log.error("Token is null, rejecting handshake. Headers: " + request.getHeaders());
            return false;
        }

        Claims claims = jwtService.validateToken(token);
        log.info("Token claims: " + claims);

        if (claims == null) {
            log.error("Invalid token, rejecting handshake.");
            return false;
        }

        String user = claims.getSubject();
        if (user == null) {
            log.error("User is null, rejecting handshake.");
            return false;
        }

        attributes.put("sub", user);
        attributes.put("token", token);

        if (usersRestApi.findByKeycloakId(user).isEmpty()) {
            log.info("Creating a new user in the database");
            CreateUserPayload userPayload = new CreateUserPayload(user);
            var tmpUser = usersRestApi.createUser(userPayload);
            log.info("User created with ID: " + tmpUser.getId());
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}