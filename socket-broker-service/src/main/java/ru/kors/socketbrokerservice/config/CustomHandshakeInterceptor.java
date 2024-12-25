package ru.kors.socketbrokerservice.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kors.socketbrokerservice.services.JwtService;

import java.io.Console;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;

    public CustomHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
        log.info("CustomHandshakeInterceptor created");
    }

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

        //БРЕД
//        var user = claims.get("preferred_username", String.class);
//        if (user == null) {
//            log.info("USER == NULL");
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return false;
//        }

        //attributes.put("token", token);


        request.getHeaders().add("token", token);

        log.info("Token in header: " + request.getHeaders().getFirst("token"));
        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // После завершения рукопожатия
    }
}