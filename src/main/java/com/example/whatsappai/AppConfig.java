
package com.example.whatsappai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${whatsapp.verify-token:change-me}")
    private String webhookVerifyToken;

    @Value("${whatsapp.access-token:}")
    private String whatsappAccessToken;

    @Value("${whatsapp.phone-number-id:}")
    private String whatsappPhoneNumberId;

    @Value("${whatsapp.graph-api-version:v23.0}")
    private String graphApiVersion;

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String geminiModel;

    @Value("${business.instructions:You are a helpful, concise customer support assistant. If you do not know an answer, say so and offer to connect the customer with a human.}")
    private String businessInstructions;

    public String getWebhookVerifyToken() {
        return webhookVerifyToken;
    }

    public String getWhatsappAccessToken() {
        return whatsappAccessToken;
    }

    public String getWhatsappPhoneNumberId() {
        return whatsappPhoneNumberId;
    }

    public String getGraphApiVersion() {
        return graphApiVersion;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public String getGeminiModel() {
        return geminiModel;
    }

    public String getBusinessInstructions() {
        return businessInstructions;
    }
}
