package com.example.adminservice.service;

import com.example.adminservice.clients.NotificationsServiceClient;
import com.example.adminservice.dtos.notification.CreateNotificationRequest;
import com.example.notificationsservice.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final NotificationsServiceClient notificationsServiceClient;

    public Notification createNotification(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());

        return notificationsServiceClient.create(notification);
    }

    public List<Notification> getNotificationsByUser(String userId) {
        return notificationsServiceClient.list(userId);
    }

    public Notification getNotificationById(String id) {
        return notificationsServiceClient.getById(id);
    }

    public Notification markRead(String id, boolean read) {
        return notificationsServiceClient.markRead(id, read);
    }

    public void deleteNotification(String id) {
        notificationsServiceClient.delete(id);
    }
}
