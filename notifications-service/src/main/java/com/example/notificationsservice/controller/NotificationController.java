package com.example.notificationsservice.controller;

import com.example.notificationsservice.client.ProfileClient;
import com.example.notificationsservice.model.Notification;
import com.example.notificationsservice.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository repo;
    private final ProfileClient profileClient;

    public NotificationController(NotificationRepository repo, ProfileClient profileClient) {
        this.repo = repo;
        this.profileClient = profileClient;
    }

    @PostMapping
    public Notification create(@RequestBody Notification n) {
        if (n.getUserId() == null || n.getUserId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        boolean exists = profileClient.userExists(n.getUserId());
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + n.getUserId());
        }

        n.setId(null);
        return repo.save(n);
    }

    @GetMapping
    public List<Notification> list(@RequestParam String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/{id}")
    public Notification getById(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));
    }

    @PutMapping("/{id}")
    public Notification update(@PathVariable String id, @RequestBody Notification updated) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));

        if (updated.getUserId() == null || updated.getUserId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        boolean exists = profileClient.userExists(updated.getUserId());
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + updated.getUserId());
        }

        existing.setUserId(updated.getUserId());
        existing.setType(updated.getType());
        existing.setMessage(updated.getMessage());
        existing.setRead(updated.isRead());

        if (updated.getCreatedAt() != null) {
            existing.setCreatedAt(updated.getCreatedAt());
        }

        return repo.save(existing);
    }

    @PatchMapping("/{id}/read")
    public Notification markRead(@PathVariable String id, @RequestParam boolean read) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));

        existing.setRead(read);
        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        Notification existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id));

        repo.delete(existing);
    }
}