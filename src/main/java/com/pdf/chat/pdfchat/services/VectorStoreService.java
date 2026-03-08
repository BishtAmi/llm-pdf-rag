package com.pdf.chat.pdfchat.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VectorStoreService {

    @Value("${qdrant.url}")
    private String qdrantUrl;

    @Value("${qdrant.collection}")
    private String collection;

    private final RestTemplate restTemplate = new RestTemplate();

    public void storeVector(String text, List<Double> vector) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("text", text);

        Map<String, Object> point = new HashMap<>();
        point.put("id", UUID.randomUUID().toString());
        point.put("vector", vector);
        point.put("payload", payload);

        Map<String, Object> body = Map.of("points", List.of(point));

        restTemplate.put(
                qdrantUrl + "/collections/" + collection + "/points?wait=true",
                body
        );
    }

    public List<String> search(List<Double> queryVector) {

        Map<String, Object> body = Map.of(
                "vector", queryVector,
                "limit", 5,
                "with_payload", true
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                qdrantUrl + "/collections/" + collection + "/points/search",
                body,
                Map.class
        );
        
        List<Map<String, Object>> results
                = (List<Map<String, Object>>) response.getBody().get("result");

        return results.stream()
                .map(r -> {
                    Map<String, Object> payload = (Map<String, Object>) r.get("payload");
                    if (payload == null) {
                        return "";
                    }
                    return (String) payload.getOrDefault("text", "");
                })
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
