package com.finpay.wallet_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long receiverId;
    // Utilizing BigDecimal to prevent floating-point rounding errors in financial calculations
    private BigDecimal amount;

    private String type; // "DEPOSIT" or "TRANSFER"

    private LocalDateTime createdAt;
    // createdAt: Stores the exact date and time the transaction completed.

    // Critical for ensuring exactly-once processing (Idempotency).
    // The database enforces uniqueness, instantly rejecting duplicate POST requests.
    @Column(nullable = false, unique = true)
    private String idempotencyKey;
}
