package com.finpay.wallet_service.repository;

import com.finpay.wallet_service.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Returns a paginated view of a user's history, handling both sent and received transactions
    Page<Transaction> findBySenderIdOrReceiverId(Long senderId, Long receiverId, Pageable pageable);

    // Critical for the idempotency layer: verifies if a specific transaction payload has already been processed
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
