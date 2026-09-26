package com.finpay.wallet_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "webhooks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Webhook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Maps back to the User entity ID
    @Column(nullable = false)
    private Long userId;
    // The external endpoint that will receive real-time asynchronous HTTP POST notifications
    @Column(nullable = false)
    private String targetUrl;
}
