package com.finpay.wallet_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Lombok automatically creates the getters and setters
public class AuthRequest {

    // Jakarta Validation rules enforce data hygiene before the request ever hits the Controller
    @Email(message = "Enter a valid email address")
    @NotBlank(message = "Email is required and cannot be blank")
    private String email;

    @NotBlank(message = "Please create a password, it can't be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}