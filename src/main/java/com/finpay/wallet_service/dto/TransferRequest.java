package com.finpay.wallet_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class TransferRequest {
    // Relying on email rather than IDs prevents enumeration attacks
    // where malicious users guess sequential user IDs.
    @NotBlank(message = "Receiver email is required and cannot be blank")
    @Email(message = "Must be a valid email address")
    private String receiverEmail;

    // Enforcing positive numbers ensures a user cannot maliciously "transfer" negative money
    // to essentially steal from another account.
    @NotNull(message = "Amount is required")
    @Positive(message = "Transfer amount must be greater than zero")
    private BigDecimal amount;
}
