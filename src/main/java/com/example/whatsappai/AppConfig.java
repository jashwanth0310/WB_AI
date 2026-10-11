package com.example.whatsappai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${WHATSAPP_VERIFY_TOKEN:change-me}")
    private String webhookVerifyToken;

    @Value("${whatsapp.access-token:}")
    private String accessToken;

    @Value("${WHATSAPP_PHONE_NUMBER_ID:}")
    private String whatsappPhoneNumberId;

    @Value("${WHATSAPP_GRAPH_API_VERSION:v23.0}")
    private String graphApiVersion;

    @Value("${GEMINI_API_KEY:}")
    private String geminiApiKey;

    @Value("${GEMINI_MODEL:gemini-2.5-flash}")
    private String geminiModel;

    @Value("${BUSINESS_INSTRUCTIONS:You are a helpful, concise customer support assistant. If you do not know an answer, say so and offer to connect the customer with a human.}")
    private String businessInstructions;

    public String getWebhookVerifyToken() { return webhookVerifyToken; }
    public String getWhatsappAccessToken() { return whatsappAccessToken; }
    public String getWhatsappPhoneNumberId() { return whatsappPhoneNumberId; }
    public String getGraphApiVersion() { return graphApiVersion; }
    public String getGeminiApiKey() { return geminiApiKey; }
    public String getGeminiModel() { return geminiModel; }
    public String getBusinessInstructions() { return businessInstructions; }
}
