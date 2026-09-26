package com.finpay.wallet_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Forces Hibernate to save the string value (e.g., "ADMIN") instead of an integer
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    @Column(columnDefinition = "boolean default true")
    private boolean isActive = true;

    // -- Spring Security UserDetails Methods --
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Appends "ROLE_" to comply with Spring Security's strict role-checking conventions
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        // Overriding default username behavior to authenticate via email
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // If isActive is false, Spring Security blocks the login.
        return this.isActive;
    }
}