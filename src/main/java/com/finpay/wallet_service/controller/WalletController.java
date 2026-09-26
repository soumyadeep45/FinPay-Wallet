package com.finpay.wallet_service.controller;

import com.finpay.wallet_service.dto.TransferRequest;
import com.finpay.wallet_service.entity.Transaction;
import com.finpay.wallet_service.entity.Wallet;
import com.finpay.wallet_service.service.WalletService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor

public class WalletController {
    private final WalletService walletService;

    @GetMapping("/balance")
    public BigDecimal getBalance(Principal principal) {

        // Get the email from the JWT
        String userEmail = principal.getName();
        // System.out.println("The server thinks the token belongs to: " + userEmail);
        return walletService.getBalance(userEmail);
    }

    @PostMapping("/deposit")
    public Wallet deposit(@RequestParam BigDecimal amount, Principal principal) {
        // Spring injects the verified email from the JWT
        String userEmail = principal.getName();
        return walletService.deposit(userEmail, amount);
    }

    @PostMapping("/transfer")
    public String transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            Principal principal // 1. Spring automatically injects the verified token identity here!
    ) {
        // 2. Extract the email securely from the token, not the JSON body
        String senderEmail = principal.getName();

        // 3. Pass the emails and amount to the service
        walletService.transfer(
                senderEmail,
                request.getReceiverEmail(),
                request.getAmount(),
                idempotencyKey
        );
        return "Transfer successful!";
    }

    @GetMapping("/transactions")
    public Page<Transaction> getTransactionHistory(Principal principal, Pageable pageable){
        String userEmail = principal.getName();
        return walletService.getTransactionHistory(userEmail, pageable);
    }
}
