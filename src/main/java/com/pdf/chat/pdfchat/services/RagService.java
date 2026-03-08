package com.pdf.chat.pdfchat.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pdf.chat.pdfchat.utils.Prompts;

@Service
public class RagService {

    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public RagService(EmbeddingService embeddingService,
            VectorStoreService vectorStoreService) {
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.chat-url}")
    private String chatUrl;

    public String askQuestion(String question, boolean flag) {
        try {
            List<Double> queryEmbedding = embeddingService.generateEmbedding(question);
            List<String> contextChunks = vectorStoreService.search(queryEmbedding);

            String context = String.join("\n\n", contextChunks);
            
            String prompt;

            if (flag) {
                prompt = Prompts.getResearchPrompt(question, context);
            } else {
                prompt = Prompts.getGeneralPrompt(question, context);
            }
            return callLLM(prompt);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String callLLM(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> textPart = Map.of(
                    "text", prompt);

            Map<String, Object> content = Map.of(
                    "parts", List.of(textPart));

            Map<String, Object> body = Map.of(
                    "contents", List.of(content));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            String url = chatUrl + "?key=" + apiKey;

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");

            Map<String, Object> firstCandidate = candidates.get(0);

            Map<String, Object> contentObj = (Map<String, Object>) firstCandidate.get("content");

            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentObj.get("parts");

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling LLM: " + e.getMessage();
        }
    }
}
