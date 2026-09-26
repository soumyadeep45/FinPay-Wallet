package com.finpay.wallet_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType; // <-- Imported MediaType for JSONimport org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")

class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegistrationAndLoginFlow() throws Exception {

        // 1. ACT & ASSERT: Simulate Postman hitting /register with JSON
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "integration@test.com",
                                    "password": "fast123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User and Wallet registered successfully!"));
        // 2. ACT & ASSERT: Simulate Postman hitting /login with JSON
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "integration@test.com",
                                    "password": "fast123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testFailedLoginWithWrongPassword() throws Exception {

        // 1. ARRANGE: Register a valid user first using JSON
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "secure@test.com",
                                    "password": "correct123"
                                }
                                """))
                .andExpect(status().isOk());

        // 2. ACT & ASSERT: Simulate a hacker trying the wrong password
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "secure@test.com",
                                    "password": "WRONG_PASSWORD"
                                }
                                """)) // <-- Wrong password inside the JSON!
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void testRegistration_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Intentionally sending a badly formatted email
        String badRequestJson = """
            {
                "email": "not-an-email",
                "password": "securepassword123"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badRequestJson))
                // The Global Exception Handler should intercept this and return a 400
                .andExpect(status().isBadRequest());
    }
}