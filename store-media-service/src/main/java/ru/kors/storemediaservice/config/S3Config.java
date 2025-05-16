package ru.kors.storemediaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;


@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder().build(); //Все ключи в переменнах окружения пк
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create()) // читает из переменных окружения пк
                .build();
    }

}
