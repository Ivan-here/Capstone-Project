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
}