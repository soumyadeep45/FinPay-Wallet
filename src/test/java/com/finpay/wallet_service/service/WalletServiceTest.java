package com.finpay.wallet_service.service;

import com.finpay.wallet_service.entity.Transaction;
import com.finpay.wallet_service.entity.User;
import com.finpay.wallet_service.entity.Wallet;
import com.finpay.wallet_service.entity.Webhook;
import com.finpay.wallet_service.exception.InsufficientBalanceException;
import com.finpay.wallet_service.repository.TransactionRepository;
import com.finpay.wallet_service.repository.UserRepository;
import com.finpay.wallet_service.repository.WalletRepository;
import com.finpay.wallet_service.repository.WebhookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    // 1. Create the fake dependencies
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WebhookRepository webhookRepository;
    @Mock
    private WebhookService webhookService;
    @Mock
    private UserRepository userRepository;

    // 2. Inject the fakes into the real service
    @InjectMocks
    private WalletService walletService;

    private User sender;
    private User receiver;
    private Wallet senderWallet;
    private Wallet receiverWallet;

    @BeforeEach
    void setUp() {
        sender = User.builder().id(1L).email("sender@test.com").build();
        receiver = User.builder().id(2L).email("receiver@test.com").build();

        senderWallet = new Wallet();
        senderWallet.setId(100L);
        senderWallet.setUser(sender); // Required because your code calls .getUser().getId()
        senderWallet.setBalance(new BigDecimal("500.00"));

        receiverWallet = new Wallet();
        receiverWallet.setId(200L);
        receiverWallet.setUser(receiver); // Required because your code calls .getUser().getId()
        receiverWallet.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void testTransfer_Success() {
        // Arrange: Tell the mocks exactly what to return when called
        when(transactionRepository.findByIdempotencyKey("test-key-123")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("receiver@test.com")).thenReturn(Optional.of(receiver));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(receiverWallet));

        Webhook mockWebhook = new Webhook();
        mockWebhook.setTargetUrl("https://example.com/webhook");
        when(webhookRepository.findByUserId(2L)).thenReturn(List.of(mockWebhook));

        // Act: Call the real method
        walletService.transfer("sender@test.com", "receiver@test.com", new BigDecimal("150.00"), "test-key-123");

        // Assert: Verify the math is correct
        assertEquals(new BigDecimal("350.00"), senderWallet.getBalance());
        assertEquals(new BigDecimal("250.00"), receiverWallet.getBalance());

        // Verify: Ensure the database save methods and webhook service were actually triggered
        verify(walletRepository, times(1)).save(senderWallet);
        verify(walletRepository, times(1)).save(receiverWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(webhookService, times(1)).sendNotification(eq("https://example.com/webhook"), anyString());
    }

    @Test
    void testTransfer_InsufficientBalance_ThrowsException() {
        // Arrange
        when(transactionRepository.findByIdempotencyKey("test-key-456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("sender@test.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByEmail("receiver@test.com")).thenReturn(Optional.of(receiver));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(receiverWallet));

        // Act & Assert
        assertThrows(InsufficientBalanceException.class, () -> {
            walletService.transfer("sender@test.com", "receiver@test.com", new BigDecimal("600.00"), "test-key-456");
        });

        // Verify data was never saved and webhooks were never sent
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(webhookService, never()).sendNotification(anyString(), anyString());
    }

    @Test
    void testTransfer_DuplicateIdempotencyKey_ThrowsException() {
        // Arrange: Simulate that the transaction key already exists
        when(transactionRepository.findByIdempotencyKey("duplicate-key")).thenReturn(Optional.of(new Transaction()));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            walletService.transfer("sender@test.com", "receiver@test.com", new BigDecimal("100.00"), "duplicate-key");
        });

        assertEquals("Duplicate transfer detected! Request already processed.", exception.getMessage());

        // Verify it stopped immediately and didn't even try to look up the users
        verify(userRepository, never()).findByEmail(anyString());
    }
}