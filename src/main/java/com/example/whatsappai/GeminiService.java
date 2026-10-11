package com.example.whatsappai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeminiService {
    private final AppConfig config;
    private final ObjectMapper mapper;
    private final RestClient restClient = RestClient.create();

    public GeminiService(AppConfig config, ObjectMapper mapper) {
        this.config = config;
        this.mapper = mapper;
    }

    public String generateReply(String userMessage) {
        if (config.getGeminiApiKey() == null || config.getGeminiApiKey().isBlank()) {
            return "Thanks for your message! The AI assistant is not configured yet. Please try again later.";
        }

        try {
            ObjectNode request = mapper.createObjectNode();
            ArrayNode contents = request.putArray("contents");
            ObjectNode content = contents.addObject();
            content.put("role", "user");
            ArrayNode parts = content.putArray("parts");
            parts.addObject().put("text",
                    config.getBusinessInstructions() + "\n\nCustomer message: " + userMessage);

            JsonNode response = restClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/"
                            + config.getGeminiModel() + ":generateContent?key=" + config.getGeminiApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode textNode = response == null ? null
                    : response.path("candidates").path(0).path("content").path("parts").path(0).path("text");

            if (textNode == null || textNode.isMissingNode() || textNode.asText().isBlank()) {
                return "Sorry, I couldn't prepare a reply just now. Please try again or contact our team.";
            }
            String reply = textNode.asText().trim();
            // WhatsApp text messages have a maximum body length; keep replies reasonably short.
            return reply.length() > 3500 ? reply.substring(0, 3490) + "..." : reply;
        } catch (Exception ex) {
            System.err.println("Gemini API error: " + ex.getMessage());
            return "Sorry, I'm having trouble answering right now. Please try again in a moment.";
        }
    }
}
