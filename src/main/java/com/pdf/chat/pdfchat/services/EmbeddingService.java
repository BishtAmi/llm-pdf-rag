package com.pdf.chat.pdfchat.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.embedding-url}")
    private String embeddingUrl;

    private final RestTemplate restTemplate;

    @java.lang.SuppressWarnings(value = "all")
    @lombok.Generated
    public EmbeddingService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Double> generateEmbedding(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // The API expects "content" -> "parts" -> "text"
        Map<String, Object> textPart = Map.of("text", text);
        Map<String, Object> content = Map.of("parts", List.of(textPart));

        // DO NOT put "model" in this map
        Map<String, Object> body = new HashMap<>();
        body.put("content", content);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Ensure there is only one '?' in the final URL
        String url = embeddingUrl + "?key=" + apiKey;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("embedding")) {
                Map<String, Object> embeddingObj = (Map<String, Object>) responseBody.get("embedding");
                List<?> rawEmbedding = (List<?>) embeddingObj.get("values");

                List<Double> embedding = new ArrayList<>();
                for (Object value : rawEmbedding) {
                    embedding.add(((Number) value).doubleValue());
                }
                return embedding;
            }
        } catch (Exception e) {
            // This will print the exact body of the error from Google
            System.err.println("Error calling Gemini API: " + e.getMessage());
            throw e;
        }
        return List.of();
    }
}
