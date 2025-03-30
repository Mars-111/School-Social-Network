package ru.kors.chatsservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter(autoApply = true)
@Slf4j
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        try {
            // Проверка на null перед сериализацией
            if (attribute == null) {
                return null;
            }
            log.info("data: " + objectMapper.writeValueAsString(attribute));
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // Логирование ошибки сериализации
            log.error("Ошибка сериализации JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        try {
            // Проверка на null перед десериализацией
            if (dbData == null) {
                return null;
            }
            return objectMapper.readTree(dbData);
        } catch (JsonProcessingException e) {
            // Логирование ошибки десериализации
            log.error("Ошибка десериализации JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка десериализации JSON", e);
        }
    }
}
