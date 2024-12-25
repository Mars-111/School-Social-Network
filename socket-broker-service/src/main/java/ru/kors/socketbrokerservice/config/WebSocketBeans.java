package ru.kors.socketbrokerservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kors.socketbrokerservice.services.JwtService;

@Configuration
@RequiredArgsConstructor
public class WebSocketBeans {
    private final JwtService jwtService;
    @Bean
    public CustomHandshakeInterceptor customHandshakeInterceptor() {
        return new CustomHandshakeInterceptor(jwtService);
    }
}
