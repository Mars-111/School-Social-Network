package helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.logging.Logger;

public class HttpRequestHelper {
    private static final Logger log = Logger.getLogger(HttpRequestHelper.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static boolean sendPostRequest(String url, Map<String, String> requestBody) {
        try {
            // Преобразуем тело запроса в JSON строку
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Создаем HTTP-запрос
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Отправляем запрос и логируем ответ
            log.info("Sending POST request to: " + url + " with body: " + jsonBody);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Проверяем код состояния и возвращаем результат
            if (response.statusCode() == 200) {
                log.info("Request succeeded with response: " + response.body());
                return true;
            } else {
                log.severe("Request failed with status code: " + response.statusCode() + " and response: " + response.body());
                return false;
            }
        } catch (Exception e) {
            log.severe("Error sending POST request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
