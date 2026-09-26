package com.finpay.wallet_service.repository;

import com.finpay.wallet_service.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);

    // --- Enterprise Concurrency Control ---
    // PESSIMISTIC_WRITE instructs Hibernate to issue a 'SELECT ... FOR UPDATE' in PostgreSQL.
    // This physically locks the database row until the active @Transactional method completes,
    // mathematically preventing race conditions where two threads read the same balance simultaneously.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdWithLock(@Param("userId") Long userId);
}
