package com.finpay.wallet_service.service;

import com.finpay.wallet_service.dto.UserResponseDTO;
import com.finpay.wallet_service.entity.User;
import com.finpay.wallet_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public Page<UserResponseDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Fetch the raw users, then map them to safe DTOs
        return userRepository.findAll(pageable)
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .isActive(user.isActive())
                        .build());
    }

    public void suspendUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not fount with ID: " + userId));

        user.setActive(false);
        userRepository.save(user);
    }
    public void unsuspendUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Reactivate their account
        user.setActive(true);

        // Save the update
        userRepository.save(user);
    }
}