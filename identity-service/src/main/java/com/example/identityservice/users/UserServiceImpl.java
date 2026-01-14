package com.example.identityservice.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public UserCredential register(String email, String rawPassword, String displayName) {
        if (userRepo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        UserCredential u = new UserCredential();
        u.setEmail(email.toLowerCase().trim());
        u.setDisplayName(displayName);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.getRoles().add(Role.SHOPPER);
        u.setStatus(UserStatus.ACTIVE);
        u.setCreatedAt(Instant.now());
        u.setUpdatedAt(Instant.now());

        UserCredential saved = userRepo.save(u);
        log.info("Registered userId={} email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    public UserCredential authenticate(String email, String rawPassword) {
        UserCredential u = userRepo.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (u.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        if (!encoder.matches(rawPassword, u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        log.info("Authenticated userId={} email={}", u.getId(), u.getEmail());
        return u;
    }

    @Override
    public UserCredential addRole(String userId, Role role) {
        UserCredential u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean added = u.getRoles().add(role);
        u.setUpdatedAt(Instant.now());
        UserCredential saved = userRepo.save(u);

        log.info("Role add userId={} role={} added={}", userId, role, added);
        return saved;
    }
}