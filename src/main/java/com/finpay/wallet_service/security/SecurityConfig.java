package com.finpay.wallet_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disabling CSRF is standard practice for stateless REST APIs using JWTs
                .csrf(csrf -> csrf.disable()) // Disable CSRF since we are using JWTs
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (Login, Register, Swagger UI)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Role-based access control: Only users with ROLE_ADMIN can hit these endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Every other endpoint requires a valid JWT
                        .anyRequest().authenticated()
                )
                // Registering my custom JWT filter to run before the standard Spring password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    // BCrypt handles the salt generation automatically, making it extremely secure against rainbow table attacks.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}