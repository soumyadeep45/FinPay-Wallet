package com.finpay.wallet_service.controller;

import com.finpay.wallet_service.dto.UserResponseDTO;
import com.finpay.wallet_service.entity.User;
import com.finpay.wallet_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/system-status")
    public ResponseEntity<String> getSystemStatus() {
        return ResponseEntity.ok("System is running perfectly. Welcome, Admin!");
    }

    @GetMapping("/users")
    public Page<UserResponseDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminService.getAllUsers(page, size);
    }

    @PatchMapping("/users/{userId}/suspend")
    public ResponseEntity<String> suspendUser(@PathVariable Long userId) {
        adminService.suspendUser(userId);
        return ResponseEntity.ok("User account has been successfully suspended.");
    }

    @PatchMapping("/users/{userId}/unsuspend")
    public ResponseEntity<String> unsuspendUser(@PathVariable Long userId) {
        adminService.unsuspendUser(userId);
        return ResponseEntity.ok("User account has been successfully reactivated.");
    }
}