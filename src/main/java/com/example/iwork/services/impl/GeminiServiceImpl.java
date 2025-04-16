package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.exceptions.GeminiApiException;
import com.example.iwork.services.GeminiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GeminiServiceImpl implements GeminiService {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model-name}")
    private String modelName;

    public GeminiServiceImpl() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String analyzeReview(CreateReviewDTO reviewDTO) {
        try {
            // Формируем JSON для запроса к Gemini API
            ObjectNode requestBody = objectMapper.createObjectNode();

            // Добавляем контент запроса
            ArrayNode contents = requestBody.putArray("contents");
            ObjectNode content = contents.addObject();

            // Добавляем части сообщения
            ArrayNode parts = content.putArray("parts");
            ObjectNode part = parts.addObject();

            // Формируем текст запроса для анализа
            String prompt = buildPrompt(reviewDTO);
            part.put("text", prompt);

            // Устанавливаем параметры генерации
            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.2);  // Низкая температура для более предсказуемых ответов
            generationConfig.put("maxOutputTokens", 100);  // Ограничиваем размер ответа
            generationConfig.put("topP", 0.95);
            generationConfig.put("topK", 40);
            requestBody.set("generationConfig", generationConfig);

            // Создаем HTTP запрос
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            // Используем современный способ создания RequestBody
            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(requestBody),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // Выполняем запрос
            try (Response response = client.newCall(request).execute()) {
                String responseBodyString = getString(response);

                // Парсим ответ
                JsonNode responseJson = objectMapper.readTree(responseBodyString);

                // Извлекаем текст из ответа
                if (responseJson.has("candidates") && responseJson.get("candidates").isArray() &&
                        !responseJson.get("candidates").isEmpty()) {

                    JsonNode candidate = responseJson.get("candidates").get(0);
                    if (candidate.has("content") && candidate.get("content").has("parts")) {
                        JsonNode parts2 = candidate.get("content").get("parts");
                        if (parts2.isArray() && !parts2.isEmpty() && parts2.get(0).has("text")) {
                            return parts2.get(0).get("text").asText();
                        }
                    }
                }

                throw new GeminiApiException("Не удалось извлечь текст из ответа Gemini API");
            }
        } catch (IOException e) {
            throw new GeminiApiException("Ошибка при взаимодействии с Gemini API: " + e.getMessage());
        }
    }

    @NotNull
    private static String getString(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new GeminiApiException("Ошибка при запросе к Gemini API: " + response.code() + " " + response.message());
        }

        // Безопасное получение тела ответа
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new GeminiApiException("Пустой ответ от Gemini API");
        }

        return responseBody.string();
    }

    private String buildPrompt(CreateReviewDTO reviewDTO) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Проанализируй следующий отзыв о компании и дай краткую оценку его тона, ")
                .append("выяви потенциально оскорбительные или неприемлемые высказывания, и сделай общее заключение ")
                .append("о том, требует ли отзыв модерации. Ответ должен быть кратким (1-3 предложения).\n\n")
                .append("Заголовок: ").append(reviewDTO.getTitle()).append("\n")
                .append("Основной текст: ").append(reviewDTO.getBody()).append("\n")
                .append("Плюсы: ").append(reviewDTO.getPros()).append("\n")
                .append("Минусы: ").append(reviewDTO.getCons()).append("\n");

        if (reviewDTO.getAdvice() != null) {
            promptBuilder.append("Советы руководству: ").append(reviewDTO.getAdvice());
        }

        return promptBuilder.toString();
    }
}