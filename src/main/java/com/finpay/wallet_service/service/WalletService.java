package com.finpay.wallet_service.service;

import com.finpay.wallet_service.entity.Transaction;
import com.finpay.wallet_service.entity.User;
import com.finpay.wallet_service.entity.Wallet;
import com.finpay.wallet_service.entity.Webhook;
import com.finpay.wallet_service.exception.BadRequestException;
import com.finpay.wallet_service.exception.InsufficientBalanceException;
import com.finpay.wallet_service.exception.ResourceNotFoundException;
import com.finpay.wallet_service.repository.TransactionRepository;
import com.finpay.wallet_service.repository.UserRepository;
import com.finpay.wallet_service.repository.WalletRepository;
import com.finpay.wallet_service.repository.WebhookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WebhookRepository webhookRepository;
    private final WebhookService webhookService;
    private final UserRepository userRepository;

    public BigDecimal getBalance(String userEmail){
        // 1. Find the user ID from the email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return wallet.getBalance();
    }

    @Transactional
    public Wallet deposit(String userEmail, BigDecimal amount) {
        // 1. Validation: Amount must be positive
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Deposit amount must be greater than zero");
        }

        // 2. Look up the user by email to get their database ID
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long userId = user.getId();

        // 3. Fetch wallet with lock and add balance
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        Wallet savedWallet = walletRepository.save(wallet);

        // 4. Audit logging
        Transaction transaction = Transaction.builder()
                .senderId(null)
                .receiverId(userId)
                .amount(amount)
                .type("DEPOSIT")
                .createdAt(LocalDateTime.now())
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

        transactionRepository.save(transaction);

        return savedWallet;
    }

    @Transactional
    public void transfer(String senderEmail, String receiverEmail, BigDecimal amount, String idempotencyKey){
        // 0. Check for duplicate request
        Optional<Transaction> existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if(existingTx.isPresent()){
            throw new BadRequestException("Duplicate transfer detected! Request already processed.");
        }

        // 1. Validation: Amount must be positive
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be greater than zero");
        }

        // 1.5 Translate the secure emails into numerical IDs for the database logic
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findByEmail(receiverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Long senderId = sender.getId();
        Long receiverId = receiver.getId();

        // 2. Determine lock order to prevent deadlocks 🔐
        Long firstLockId = Math.min(senderId, receiverId);
        Long secondLockId = Math.max(senderId, receiverId);

        // 2.5. Fetch and lock wallets in order 🗄️
        Wallet firstWallet = walletRepository.findByUserIdWithLock(firstLockId).orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        Wallet secondWallet = walletRepository.findByUserIdWithLock(secondLockId).orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        Wallet senderWallet;
        Wallet receiverWallet;

        if(firstWallet.getUser().getId().equals(senderId)){
            senderWallet = firstWallet;
            receiverWallet = secondWallet;
        } else {
            senderWallet = secondWallet;
            receiverWallet = firstWallet;
        }

        // 3. Check sufficient balance
        if(senderWallet.getBalance() == null || senderWallet.getBalance().compareTo(amount) < 0){
            throw new InsufficientBalanceException("Insufficient Balance!");
        }

        // 4. Update balances
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        // 5. Save changes back to database
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // 6. Audit logging
        Transaction transaction = Transaction.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .amount(amount)
                .type("TRANSFER")
                .createdAt(LocalDateTime.now())
                .idempotencyKey(idempotencyKey)
                .build();

        transactionRepository.save(transaction);

        // 7. Send Webhook Notifications (Async)
        List<Webhook> receiverWebhooks = webhookRepository.findByUserId(receiverId);

        for(Webhook webhook : receiverWebhooks){
            String message = "You just received ₹" + amount + " from User ID " + senderId;
            webhookService.sendNotification(webhook.getTargetUrl(), message);
        }
    }

    public Page<Transaction> getTransactionHistory(String userEmail, Pageable pageable){
        //  Find the user ID from the email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return transactionRepository.findBySenderIdOrReceiverId(user.getId(), user.getId(), pageable);
    }
}
