package com.finpay.wallet_service.seeder;

import com.finpay.wallet_service.entity.Role;
import com.finpay.wallet_service.entity.User;
import com.finpay.wallet_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Using CommandLineRunner ensures this code executes exactly once, right after the Spring context loads.
    @Override
    public void run(String... args) throws Exception{
        String adminEmail = "admin@finpay.com";
        // check if admin already exist or not. if not create new admin.
        if(!userRepository.findByEmail(adminEmail).isPresent()) {
            System.out.println("Admin not found. Creating a new Admin user...");
            User newAdminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(newAdminUser);
            System.out.println("Admin user successfully created!");

        } else{
            System.out.println("Admin user already exists. Skipping setup.");
        }
    }
}
