package com.finpay.wallet_service.repository;

import com.finpay.wallet_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // @Repository: Tells Spring that this interface is a Data Access Object (DAO) component so Spring can manage it as a bean.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
