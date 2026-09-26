package com.finpay.wallet_service.security;

import com.finpay.wallet_service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service

public class JwtService {
    // This is a massive, cryptographic password. It is a 256-bit Base64 encoded string.
    // DEV ONLY: Hardcoded for portfolio demonstration.
    // In production, this must be injected via Environment Variables
    private static final String SECRET_KEY = "K34VFiiu0qar9xWICc9PPCt+FRYortKmq/cViAnPTzw=";

    // 1. Converts our string into a secure Cryptographic Key object
    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 2. Generates a token when a user logs in
    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail()) // Store the user's email inside the token
                .claim("role", user.getRole())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24)) // Token expires in 24 hours
                .signWith(getSigningKey()) // Stamp it with our secret cryptographic signature
                .compact();
    }

    // 3. Reads the token when a user makes an API request
    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey()) // If a hacker tampered with the token, this line instantly crashes and denies them!
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // Returns the email that has been stored earlier
    }

    public String extractRole(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

    }
}
