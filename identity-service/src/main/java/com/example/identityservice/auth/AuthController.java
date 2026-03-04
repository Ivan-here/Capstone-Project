package com.example.identityservice.auth;

import com.example.identityservice.auth.dto.AuthResponse;
import com.example.identityservice.auth.dto.LoginRequest;
import com.example.identityservice.auth.dto.RegisterRequest;
import com.example.identityservice.users.UserCredential;
import com.example.identityservice.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {

        UserCredential u = userService.register(
                req.email(),
                req.password(),
                req.username(),
                req.firstName(),
                req.lastName(),
                req.displayName() // optional
        );

        String token = jwtService.issueAccessToken(u.getId(), u.getRoles(), u.getDisplayName());
        log.info("Issued JWT on register userId={}", u.getId());

        return new AuthResponse(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                u.getFirstName(),
                u.getLastName(),
                u.getDisplayName(),
                u.getRoles(),
                u.getStatus(),
                token,
                jwtService.accessTokenTtlSeconds()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {

        UserCredential u = userService.authenticate(req.login(), req.password());

        String token = jwtService.issueAccessToken(u.getId(), u.getRoles(), u.getDisplayName());
        log.info("Issued JWT on login userId={}", u.getId());

        return new AuthResponse(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                u.getFirstName(),
                u.getLastName(),
                u.getDisplayName(),
                u.getRoles(),
                u.getStatus(),
                token,
                jwtService.accessTokenTtlSeconds()
        );
    }
}