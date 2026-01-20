package com.example.notificationsservice.controller;

import org.springframework.web.bind.annotation.*;

import com.example.notificationsservice.model.Notification;
import com.example.notificationsservice.repository.NotificationRepository;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository repo;

    public NotificationController(NotificationRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Notification create(@RequestBody Notification n) {
        n.setId(null); // let Mongo generate id
        return repo.save(n);
    }

    @GetMapping
    public List<Notification> list(@RequestParam String userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}