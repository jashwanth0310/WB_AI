package com.example.whatsappai;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {
    private final WhatsAppService whatsAppService;
    private final AppConfig appConfig;

    public WhatsAppWebhookController(WhatsAppService whatsAppService, AppConfig appConfig) {
        this.whatsAppService = whatsAppService;
        this.appConfig = appConfig;
    }

    // Meta calls this endpoint when you configure and verify the webhook.
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode) && appConfig.getWebhookVerifyToken().equals(verifyToken)) {
            return ResponseEntity.ok(challenge == null ? "" : challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Webhook verification failed");
    }

    // Meta sends incoming messages to this endpoint.
    @PostMapping
    public ResponseEntity<Void> receiveMessage(@RequestBody JsonNode payload) {
        try {
            whatsAppService.processWebhook(payload);
        } catch (Exception ex) {
            // Keep the webhook response predictable. Check application logs for details.
            System.err.println("Could not process WhatsApp webhook: " + ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
