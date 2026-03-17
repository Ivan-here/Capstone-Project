package com.example.adminservice.controller;

import com.example.adminservice.dtos.notification.CreateNotificationRequest;
import com.example.adminservice.dtos.notification.Notification;
import com.example.adminservice.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @PostMapping
    public Notification createNotification(@RequestBody CreateNotificationRequest request) {
        return adminNotificationService.createNotification(request);
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable String userId) {
        return adminNotificationService.getNotificationsByUser(userId);
    }

    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable String id) {
        return adminNotificationService.getNotificationById(id);
    }

    @PutMapping("/{id}")
    public Notification updateNotification(
            @PathVariable String id,
            @RequestBody Notification notification
    ) {
        return adminNotificationService.updateNotification(id, notification);
    }

    @PatchMapping("/{id}/read")
    public Notification markRead(
            @PathVariable String id,
            @RequestParam boolean read
    ) {
        return adminNotificationService.markRead(id, read);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable String id) {
        adminNotificationService.deleteNotification(id);
    }
}