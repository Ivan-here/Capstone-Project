package com.example.notificationsservice.controller;

import com.example.notificationsservice.client.IdentityClient;
import com.example.notificationsservice.model.Notification;
import com.example.notificationsservice.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository repo;
    private final IdentityClient identityClient;

    public NotificationController(NotificationRepository repo, IdentityClient identityClient) {
        this.repo = repo;
        this.identityClient = identityClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification create(@RequestBody Notification notification) {
        validatePayload(notification);

        notification.setId(null);
        notification.setRead(false);
        notification.setCreatedAt(notification.getCreatedAt() != null ? notification.getCreatedAt() : Instant.now());
        hydrateUserSnapshots(notification);

        return repo.save(notification);
    }

    @GetMapping
    public List<Notification> list(
            @RequestParam String userId,
            @RequestParam(required = false) Boolean read
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        String normalizedUserId = userId.trim();
        if (read == null) {
            return repo.findByUserIdOrderByCreatedAtDesc(normalizedUserId);
        }
        return repo.findByUserIdAndReadOrderByCreatedAtDesc(normalizedUserId, read);
    }

    @GetMapping("/{id}")
    public Notification getById(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));
    }

    @GetMapping("/unread-count")
    public long unreadCount(@RequestParam String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        return repo.countByUserIdAndReadFalse(userId.trim());
    }

    @PutMapping("/{id}")
    public Notification update(@PathVariable String id, @RequestBody Notification updated) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));

        validatePayload(updated);

        existing.setUserId(updated.getUserId().trim());
        existing.setActorUserId(normalizeNullable(updated.getActorUserId()));
        existing.setType(updated.getType().trim());
        existing.setTitle(normalizeNullable(updated.getTitle()));
        existing.setMessage(updated.getMessage().trim());
        existing.setSourceService(normalizeNullable(updated.getSourceService()));
        existing.setReferenceType(normalizeNullable(updated.getReferenceType()));
        existing.setReferenceId(normalizeNullable(updated.getReferenceId()));
        existing.setTargetUrl(normalizeNullable(updated.getTargetUrl()));
        existing.setRead(updated.isRead());
        existing.setCreatedAt(updated.getCreatedAt() != null ? updated.getCreatedAt() : existing.getCreatedAt());

        hydrateUserSnapshots(existing);
        return repo.save(existing);
    }

    @PatchMapping("/{id}/read")
    public Notification markRead(@PathVariable String id, @RequestParam boolean read) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));

        existing.setRead(read);
        return repo.save(existing);
    }

    @PatchMapping("/user/{userId}/read-all")
    public List<Notification> markAllRead(@PathVariable String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        List<Notification> notifications = repo.findByUserIdAndReadOrderByCreatedAtDesc(userId.trim(), false);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        return repo.saveAll(notifications);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));

        repo.delete(existing);
    }

    private void validatePayload(Notification notification) {
        if (notification.getUserId() == null || notification.getUserId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (notification.getType() == null || notification.getType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (notification.getMessage() == null || notification.getMessage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message is required");
        }
    }

    private void hydrateUserSnapshots(Notification notification) {
        IdentityClient.IdentityUserSummary recipient = identityClient.getUserSummary(notification.getUserId().trim());
        if (recipient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + notification.getUserId());
        }

        notification.setUserId(recipient.userId());
        notification.setRecipientDisplayName(recipient.displayName());
        notification.setRecipientUsername(recipient.username());
        notification.setType(notification.getType().trim());
        notification.setMessage(notification.getMessage().trim());
        notification.setTitle(normalizeNullable(notification.getTitle()));
        notification.setSourceService(normalizeNullable(notification.getSourceService()));
        notification.setReferenceType(normalizeNullable(notification.getReferenceType()));
        notification.setReferenceId(normalizeNullable(notification.getReferenceId()));
        notification.setTargetUrl(normalizeNullable(notification.getTargetUrl()));

        String actorUserId = normalizeNullable(notification.getActorUserId());
        notification.setActorUserId(actorUserId);
        if (actorUserId != null) {
            IdentityClient.IdentityUserSummary actor = identityClient.getUserSummary(actorUserId);
            if (actor == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Actor user not found: " + actorUserId);
            }
            notification.setActorDisplayName(actor.displayName());
            notification.setActorUsername(actor.username());
        } else {
            notification.setActorDisplayName(null);
            notification.setActorUsername(null);
        }
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
