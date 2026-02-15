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
    public UserCredential register(String email, String rawPassword, String username, String firstName, String lastName, String displayName) {
        String normalizedEmail = email == null ? null : email.toLowerCase().trim();
        String normalizedUsername = username == null ? null : username.trim();
        String normalizedFirstName = firstName == null ? null : firstName.trim();
        String normalizedLastName = lastName == null ? null : lastName.trim();
        String normalizedDisplayName = displayName == null ? null : displayName.trim();

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (normalizedUsername == null || normalizedUsername.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (normalizedFirstName == null || normalizedFirstName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name is required");
        }
        if (normalizedLastName == null || normalizedLastName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (userRepo.existsByUsername(normalizedUsername)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        //displayName not provided - can generate it
        if (normalizedDisplayName == null || normalizedDisplayName.isBlank()) {
            normalizedDisplayName = normalizedFirstName + " " + normalizedLastName;
        }

        Instant now = Instant.now();

        UserCredential u = new UserCredential();
        u.setEmail(normalizedEmail);
        u.setUsername(normalizedUsername);
        u.setFirstName(normalizedFirstName);
        u.setLastName(normalizedLastName);
        u.setDisplayName(normalizedDisplayName);

        u.setPasswordHash(encoder.encode(rawPassword));
        u.getRoles().add(Role.SHOPPER);
        u.setStatus(UserStatus.ACTIVE);
        u.setCreatedAt(now);
        u.setUpdatedAt(now);

        UserCredential saved = userRepo.save(u);
        log.info("Registered userId={} email={} username={}", saved.getId(), saved.getEmail(), saved.getUsername());
        return saved;
    }

    @Override
    public UserCredential authenticate(String login, String rawPassword) {
        String normalizedLogin = login == null ? null : login.trim();
        if (normalizedLogin == null || normalizedLogin.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or username is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        String emailCandidate = normalizedLogin.toLowerCase();

        UserCredential u = userRepo.findByEmailOrUsername(emailCandidate, normalizedLogin)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (u.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        if (!encoder.matches(rawPassword, u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        log.info("Authenticated userId={} email={} username={}", u.getId(), u.getEmail(), u.getUsername());
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