# WhatsApp AI Auto-Reply (Java + Spring Boot + Gemini)

A starter project that receives **plain-text** WhatsApp messages through Meta's WhatsApp Cloud API, asks Gemini to draft a response, and sends the response back to the customer.

## Requirements

- Java 17+
- Maven 3.9+ (or use your IDE's Maven support)
- A Meta developer app with WhatsApp Cloud API configured
- A Gemini API key from Google AI Studio
- A public HTTPS URL for the webhook during Meta verification/testing (a tunnel is fine for local development)

## 1. Configure environment variables

Set the variables from `.env.example` in your IDE run configuration, terminal, or hosting dashboard:

- `WHATSAPP_VERIFY_TOKEN`: a secret string you choose; must match the token entered in Meta's webhook setup
- `WHATSAPP_ACCESS_TOKEN`: access token from your Meta WhatsApp API setup
- `WHATSAPP_PHONE_NUMBER_ID`: the phone number ID shown in the WhatsApp API setup
- `GEMINI_API_KEY`: your Gemini API key
- `GEMINI_MODEL`: defaults to `gemini-2.5-flash`
- `BUSINESS_INSTRUCTIONS`: instructions describing your business and how the bot should respond

Do not paste real keys into source code or commit them to GitHub.

## 2. Run locally

From this project directory:

```bash
mvn spring-boot:run
```

The server runs at `http://localhost:8080`.

## 3. Expose your local webhook

Meta must reach your webhook over a public HTTPS URL. Use a tunneling service such as ngrok during development, then set the callback URL in Meta to:

```text
https://YOUR-PUBLIC-HTTPS-URL/webhook
```

The verification token must equal `WHATSAPP_VERIFY_TOKEN`.

## 4. Configure Meta webhook

In your Meta developer app:

1. Add/configure the WhatsApp product and complete the Cloud API test setup.
2. Open the WhatsApp webhook configuration.
3. Set callback URL to `https://YOUR-PUBLIC-HTTPS-URL/webhook`.
4. Enter the same `WHATSAPP_VERIFY_TOKEN`.
5. Subscribe to the `messages` webhook field.
6. Send a message to your test WhatsApp number from an allowed test recipient.

Meta's interface changes over time; follow its current Cloud API setup instructions if menu names differ.

## 5. How it works

- `GET /webhook`: verifies the callback URL.
- `POST /webhook`: receives message notifications.
- `GeminiService`: generates the reply.
- `WhatsAppService`: sends the reply through Meta's Graph API.

## 6. Customize the AI

Change `BUSINESS_INSTRUCTIONS` in the environment. Example:

```text
You are the assistant for ABC Clinic. Opening hours are Monday-Saturday, 9 AM to 6 PM.
Never invent doctor availability, prices, or medical advice. For appointment requests,
ask for the preferred doctor and date, and tell the user that staff will confirm availability.
```

Important: the starter does not connect to a clinic database, confirm appointments, remember conversation history, or support images/audio. Add those features explicitly before relying on it for real business operations.

## 7. Before production

This is a learning starter, not a hardened production service. Before serving customers:

- Validate Meta webhook signatures (`X-Hub-Signature-256`) using your Meta app secret.
- Add idempotency/deduplication so webhook retries do not send duplicate replies.
- Add rate limiting, timeouts, retries, logging, monitoring, and error handling.
- Store secrets in your host's secret manager/environment variables.
- Add a human handoff path and make clear that users are talking to an AI assistant.
- Follow Meta's current messaging rules, template requirements, opt-in rules, and pricing.
- Avoid collecting or logging sensitive customer data unnecessarily.

## Common problems

- **Webhook verification fails:** `WHATSAPP_VERIFY_TOKEN` must exactly match the token in Meta.
- **No reply is sent:** check `WHATSAPP_ACCESS_TOKEN`, `WHATSAPP_PHONE_NUMBER_ID`, and server logs.
- **Gemini does not respond:** check `GEMINI_API_KEY`, model availability, API quota, and logs.
- **Meta cannot reach localhost:** use a public HTTPS tunnel or deploy the app.
