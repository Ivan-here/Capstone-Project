package com.example.identityservice.internal;

import com.example.identityservice.internal.dto.AddRoleRequest;
import com.example.identityservice.internal.dto.InternalUserSummaryResponse;
import com.example.identityservice.users.UserCredential;
import com.example.identityservice.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{userId}/summary")
    public InternalUserSummaryResponse getSummary(@PathVariable String userId) {
        UserCredential user = userService.getUserById(userId);
        log.info("Internal summary lookup userId={}", userId);

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .sorted()
                .toList();

        return new InternalUserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus().name(),
                roles
        );
    }

    @PostMapping("/{userId}/roles:add")
    public Map<String, Object> addRole(
            @PathVariable String userId,
            @Valid @RequestBody AddRoleRequest req
    ) {
        UserCredential updated = userService.addRole(userId, req.role());
        log.info("Internal role add userId={} role={}", userId, req.role());

        return Map.of(
                "userId", updated.getId(),
                "roles", updated.getRoles()
        );
    }

    @DeleteMapping("/{userId}/roles:remove")
    public Map<String, Object> removeRole(
            @PathVariable String userId,
            @Valid @RequestBody AddRoleRequest req
    ) {
        UserCredential updated = userService.removeRole(userId, req.role());
        log.info("Internal role remove userId={} role={}", userId, req.role());

        return Map.of(
                "userId", updated.getId(),
                "roles", updated.getRoles()
        );
    }
}
