package com.example.datn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("🚀 Nhận request từ frontend:");
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBody));

            HttpClient client = HttpClient.newHttpClient();

            String json = objectMapper.writeValueAsString(requestBody);
            System.out.println("📤 Gửi request đến OpenRouter:\n" + json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openRouterApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📥 Nhận phản hồi từ OpenRouter:");
            System.out.println(response.body());

            return ResponseEntity.ok(objectMapper.readTree(response.body()));

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gọi API OpenRouter:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}