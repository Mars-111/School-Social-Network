package ru.kors.socketbrokerservice.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kors.socketbrokerservice.api.UsersRestApi;
import ru.kors.socketbrokerservice.api.payload.CreateUserPayload;
import ru.kors.socketbrokerservice.api.payload.UserRestPayload;
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
        // Получите токен из параметров запроса
        List<String> tokenList =
                UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().get("token");
        String token = (tokenList != null && !tokenList.isEmpty()) ? tokenList.get(0) : null;

        if (token == null) {
            log.info("Token is null");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        Claims claims = jwtService.validateToken(token);
        if (claims == null) {
            log.info("Invalid token");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String user = claims.getSubject();
        if (user == null) {
            log.info("User is null");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        attributes.put("sub", user);

        if  (usersRestApi.findByKeycloakId(user).isEmpty()) {
            log.info("Creating a new user in the database");
            CreateUserPayload userPayload = new CreateUserPayload(user);
            var tmpUser  = usersRestApi.createUser(userPayload);
            log.info(String.valueOf(tmpUser.getId()));
        }

        request.getHeaders().add("token", token);

        log.info("Token in header: " + request.getHeaders().getFirst("token"));
        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // После завершения рукопожатия
    }
}