package com.finpay.wallet_service.dto;

import com.finpay.wallet_service.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String email;
    private Role role;
    private boolean isActive;
}