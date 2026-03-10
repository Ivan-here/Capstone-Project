package com.example.identityservice.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

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

    @Override
    public UserCredential removeRole(String userId, Role role) {
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }

        UserCredential u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!u.getRoles().contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have this role");
        }

        if (u.getRoles().size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must have at least one role");
        }

        boolean removed = u.getRoles().remove(role);
        u.setUpdatedAt(Instant.now());
        UserCredential saved = userRepo.save(u);

        log.info("Role remove userId={} role={} removed={}", userId, role, removed);
        return saved;
    }

    @Override
    public UserCredential updateStatus(String userId, UserStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        UserCredential u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserStatus oldStatus = u.getStatus();
        u.setStatus(status);
        u.setUpdatedAt(Instant.now());

        UserCredential saved = userRepo.save(u);
        log.info("Status update userId={} oldStatus={} newStatus={}", userId, oldStatus, status);

        return saved;
    }

    @Override
    public UserCredential updateUserDetails(String userId,
                                            String firstName,
                                            String lastName,
                                            String displayName,
                                            String email,
                                            String username,
                                            UserStatus status) {
        UserCredential u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String normalizedFirstName = firstName == null ? null : firstName.trim();
        String normalizedLastName = lastName == null ? null : lastName.trim();
        String normalizedDisplayName = displayName == null ? null : displayName.trim();
        String normalizedEmail = email == null ? null : email.toLowerCase().trim();
        String normalizedUsername = username == null ? null : username.trim();

        if (normalizedFirstName == null || normalizedFirstName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name is required");
        }
        if (normalizedLastName == null || normalizedLastName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name is required");
        }
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (normalizedUsername == null || normalizedUsername.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        if (!u.getEmail().equalsIgnoreCase(normalizedEmail) && userRepo.existsByEmail(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (!u.getUsername().equals(normalizedUsername) && userRepo.existsByUsername(normalizedUsername)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (normalizedDisplayName == null || normalizedDisplayName.isBlank()) {
            normalizedDisplayName = normalizedFirstName + " " + normalizedLastName;
        }

        u.setFirstName(normalizedFirstName);
        u.setLastName(normalizedLastName);
        u.setDisplayName(normalizedDisplayName);
        u.setEmail(normalizedEmail);
        u.setUsername(normalizedUsername);
        u.setStatus(status);
        u.setUpdatedAt(Instant.now());

        UserCredential saved = userRepo.save(u);
        log.info("Updated user details userId={} email={} username={}", saved.getId(), saved.getEmail(), saved.getUsername());

        return saved;
    }

    @Override
    public List<UserCredential> getAllUsers() {
        List<UserCredential> users = userRepo.findAll();
        log.info("Fetched all users");
        return users;
    }

    @Override
    public List<UserCredential> getUsersByStatus(UserStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        List<UserCredential> users = userRepo.findByStatus(status);
        log.info("Fetched users by status={} count={}", status, users.size());
        return users;
    }

    @Override
    public List<UserCredential> getUsersByRole(Role role) {
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required");
        }

        List<UserCredential> users = userRepo.findByRolesContaining(role);
        log.info("Fetched users by role={} count={}", role, users.size());
        return users;
    }
    @Override
    public List<UserCredential> searchUsers(String query) {
        String normalizedQuery = query == null ? "" : query.trim();

        if (normalizedQuery.isBlank()) {
            List<UserCredential> users = userRepo.findAll();
            log.info("Search query blank, returning all users count={}", users.size());
            return users;
        }

        List<UserCredential> users = userRepo.searchUsers(normalizedQuery);
        log.info("Searched users query='{}' count={}", normalizedQuery, users.size());
        return users;
    }
    @Override
    public void deleteUser(String userId) {
        UserCredential u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRepo.delete(u);
        log.info("Deleted user userId={} email={} username={}", u.getId(), u.getEmail(), u.getUsername());
    }

    @Override
    public UserCredential getUserById(String userId) {
        UserCredential user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        log.info("Fetched user by id={}", userId);
        return user;
    }

}