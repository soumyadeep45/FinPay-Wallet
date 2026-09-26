package com.finpay.wallet_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2,nullable = false)
    private BigDecimal balance;

    // Establishes a strict 1:1 relationship; a wallet cannot exist without a user.
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
