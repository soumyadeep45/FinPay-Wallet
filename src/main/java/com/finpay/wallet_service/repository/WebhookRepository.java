package com.finpay.wallet_service.repository;

import com.finpay.wallet_service.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    // Retrieves all active webhooks registered to a specific user to broadcast notifications
    List<Webhook> findByUserId(Long userId);
}
