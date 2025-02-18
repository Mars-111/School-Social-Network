package ru.kors.chatsservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic chatTopic() {
        return new NewTopic("test", 10, (short) 2);
    }
}
