package com.finpay.wallet_service.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service

public class WebhookService {
    private final RestClient restClient;

    public WebhookService(){
        this.restClient = RestClient.create();
    }

    @Async
    public void sendNotification(String targetUrl, String message){
        // 1. Prepare the JSON body
        Map<String, String> payload = Map.of("message",message);

        // 2. Fire the HTTP POST request!
        try{
            restClient.post()                  // Select POST method
                    .uri(targetUrl)            // Enter the target URL
                    .body(payload)             // Attach the JSON body
                    .retrieve()                // Execute the request
                    .toBodilessEntity();       // We don't care about the response body, just that it sent

            System.out.println("✅ Webhook sent successfully to: " + targetUrl);
        } catch (Exception e){
            // 3. The Safety Net
            System.out.println("❌ Failed to send webhook to " + targetUrl + " - Error: " + e.getMessage());

        }
    }
}
