package com.example.whatsappai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WhatsAppService {
    private final AppConfig config;
    private final GeminiService geminiService;
    private final ObjectMapper mapper;
    private final RestClient restClient = RestClient.create();

    public WhatsAppService(AppConfig config, GeminiService geminiService, ObjectMapper mapper) {
        this.config = config;
        this.geminiService = geminiService;
        this.mapper = mapper;
    }

    public void processWebhook(JsonNode payload) {
        JsonNode messages = payload.path("entry").path(0)
                .path("changes").path(0).path("value").path("messages");

        // Status notifications have no "messages" array. Ignore them.
        if (!messages.isArray()) {
            return;
        }

        for (JsonNode message : messages) {
            String from = message.path("from").asText("");
            String type = message.path("type").asText("");

            // Starter supports plain text messages. Add image/audio handling separately if needed.
            if (from.isBlank() || !"text".equals(type)) {
                continue;
            }

            String userText = message.path("text").path("body").asText("");
            if (userText.isBlank()) {
                continue;
            }

            System.out.println("WhatsApp message from " + from + ": " + userText);
            String reply = geminiService.generateReply(userText);
            sendTextMessage(from, reply);
        }
    }

    public void sendTextMessage(String recipientPhoneNumber, String messageText) {
        if (config.getWhatsappAccessToken() == null || config.getWhatsappAccessToken().isBlank()
                || config.getWhatsappPhoneNumberId() == null || config.getWhatsappPhoneNumberId().isBlank()) {
            System.err.println("WhatsApp API is not configured. Reply not sent.");
            return;
        }

        ObjectNode body = mapper.createObjectNode();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", recipientPhoneNumber);
        body.put("type", "text");
        body.putObject("text").put("preview_url", false).put("body", messageText);

        String url = "https://graph.facebook.com/" + config.getGraphApiVersion()
                + "/" + config.getWhatsappPhoneNumberId() + "/messages";

        restClient.post()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(config.getWhatsappAccessToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
