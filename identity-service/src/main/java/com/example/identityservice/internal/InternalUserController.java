package com.example.identityservice.internal;

import com.example.identityservice.internal.dto.AddRoleRequest;
import com.example.identityservice.users.UserCredential;
import com.example.identityservice.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @Value("${internal.sharedSecret}")
    private String expectedSecret;

    @PostMapping("/{userId}/roles:add")
    public Map<String, Object> addRole(
            @PathVariable String userId,
            @RequestHeader(name = "X-Internal-Secret", required = false) String secret,
            @Valid @RequestBody AddRoleRequest req
    ) {
        if (secret == null || !secret.equals(expectedSecret)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        UserCredential updated = userService.addRole(userId, req.role());
        log.info("Internal role add userId={} role={}", userId, req.role());

        return Map.of(
                "userId", updated.getId(),
                "roles", updated.getRoles()
        );
    }
}