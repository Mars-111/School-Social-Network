package ru.kors.socketbrokerservice.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.converter.MessageConverter;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import java.util.List;
//
//@Configuration
//@EnableWebSocketMessageBroker
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    private final CustomHandshakeInterceptor customHandshakeInterceptor;
//
//    @Value("${allowed.origins:http://localhost:3000,http://localhost:5173}") // Можно задавать через application.properties
//    private String allowedOrigins;
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*")
//                .addInterceptors(customHandshakeInterceptor)
//                .withSockJS();
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/app");
//
//        registry.enableSimpleBroker("/topic", "/system")
//                .setHeartbeatValue(new long[]{10000, 30000}) // Heartbeat 10s -> Client, 30s <- Server
//                .setTaskScheduler(taskScheduler()); // Используем таск-менеджер
//    }
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(1);
//        scheduler.setThreadNamePrefix("ws-heartbeat-thread-");
//        scheduler.initialize();
//        return scheduler;
//    }
//
//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> converters) {
//        converters.add(new MappingJackson2MessageConverter());
//        return false;
//    }
//}


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Включаем поддержку SockJS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat", "/private");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
