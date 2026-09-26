package com.finpay.wallet_service.controller;

import com.finpay.wallet_service.entity.User;
import com.finpay.wallet_service.entity.Webhook;
import com.finpay.wallet_service.exception.ResourceNotFoundException;
import com.finpay.wallet_service.repository.UserRepository;
import com.finpay.wallet_service.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor

public class WebhookController {
    private final WebhookRepository webhookRepository;
    private final UserRepository userRepository;

    @PostMapping
    public String registerWebhook(@RequestParam String targetUrl, Principal principal){
        // 1. Get the verified email from the token
        String userEmail = principal.getName();

        // 2. Look up their true ID from the database
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Webhook webhook = Webhook.builder()
                .userId(user.getId()) // 100% secure, tamper-proof ID
                .targetUrl(targetUrl)
                .build();

        webhookRepository.save(webhook);
        return "Webhook saved successfully!";
    }
}
