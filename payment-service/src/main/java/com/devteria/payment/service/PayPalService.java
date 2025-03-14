package com.devteria.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PayPalService {

    @Value("${paypal.api.base-url}")
    private String baseUrl;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.secret}")
    private String secret;

    private final RestTemplate restTemplate;
    private String accessToken;
    private LocalDateTime tokenExpiryTime;

    public PayPalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches a PayPal API access token with caching and expiration management.
     * @return Valid PayPal access token.
     */
    public synchronized String getAccessToken() {
        // ✅ Use cached token if valid
        if (accessToken != null && LocalDateTime.now().isBefore(tokenExpiryTime)) {
            return accessToken;
        }

        // 🔐 Encode credentials for Basic Auth
        String auth = clientId + ":" + secret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

        // 🌍 Make API call to get new token
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/v1/oauth2/token",
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                // 🔄 Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                accessToken = root.path("access_token").asText();
                int expiresIn = root.path("expires_in").asInt();

                // 🕒 Set expiry time (refresh 1 min early)
                tokenExpiryTime = LocalDateTime.now().plusSeconds(expiresIn - 60);

                return accessToken;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse PayPal token response", e);
            }
        }
        throw new RuntimeException("Failed to get PayPal Access Token");
    }

    /**
     * Processes a refund for a given PayPal transaction.
     * @param transactionId PayPal Transaction ID.
     * @param refundAmount Amount to refund.
     * @param currency Currency code (e.g., "USD").
     * @return Refund transaction ID.
     */
    public String refundPayment(String transactionId, BigDecimal refundAmount, String currency) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 🟢 Build refund request body
        JSONObject requestBody = new JSONObject();
        JSONObject amount = new JSONObject();
        amount.put("value", refundAmount.toString());
        amount.put("currency_code", currency);
        requestBody.put("amount", amount);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        // 🔄 Call PayPal Refund API
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/v2/payments/captures/" + transactionId + "/refund",
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.CREATED) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("id"); // Refund transaction ID
        }
        throw new RuntimeException("Refund Failed: " + response.getBody());
    }
}
