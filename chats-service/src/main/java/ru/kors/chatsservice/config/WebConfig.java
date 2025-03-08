//package ru.kors.chatsservice.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@EnableWebMvc
//public class WebConfig implements WebMvcConfigurer {
//
////    @Override
////    public void addCorsMappings(CorsRegistry registry) {
////        registry.addMapping("/internal/api/**")
////                .allowedOrigins("http://localhost:8081", "http://localhost:9082", "http://localhost:5173") //сокет сервис и др
////                .allowedMethods("GET", "POST", "PUT", "DELETE")
////                .allowCredentials(true);
////        registry.addMapping("/api/**")
////                .allowedOrigins("http://localhost:5173") // Разрешаем фронтенд
////                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
////                .allowedHeaders("Authorization", "Content-Type") // Разрешаем заголовок токена
////                .exposedHeaders("Authorization") // Делаем заголовок видимым
////                .allowCredentials(true);
////    }
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**")
//                .allowedOrigins("http://localhost:5173") // Разрешаем фронтенду делать запросы
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешаем все методы
//                .allowedHeaders("Authorization", "Content-Type") // Разрешаем передавать заголовок Authorization
//                .exposedHeaders("Authorization") // Делаем заголовок видимым в ответах
//                .allowCredentials(true); // Разрешаем передачу куков и заголовков авторизации
//    }
//
//}
