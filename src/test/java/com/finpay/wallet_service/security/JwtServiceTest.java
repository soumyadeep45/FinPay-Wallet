package com.finpay.wallet_service.security;

import com.finpay.wallet_service.entity.Role;
import com.finpay.wallet_service.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void testGenerateAndExtractToken() {
        // 1. Arrange (Set up the initial data)
        JwtService jwtService = new JwtService();

        // Create a dummy User object instead of just a String email
        User testUser = User.builder()
                .email("alice@test.com")
                .role(Role.USER)
                .build();

        // 2. Act (Run the methods we want to test)
        String token = jwtService.generateToken(testUser);
        String extractedEmail = jwtService.extractEmail(token);
        String extractedRole = jwtService.extractRole(token);

        // 3. Assert (Did the code do what we expected?)
        assertNotNull(token, "The generated token should not be null");
        assertEquals("alice@test.com", extractedEmail, "The extracted email should match the original");
        assertEquals("USER", extractedRole, "The extracted role should match the user's role");
    }
}