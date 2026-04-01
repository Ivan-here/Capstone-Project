package com.example.adminservice.service;

import com.example.adminservice.clients.NotificationsServiceClient;
import com.example.adminservice.dtos.notification.CreateNotificationRequest;
import com.example.adminservice.dtos.notification.Notification;
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
        notification.setActorUserId(request.getActorUserId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setSourceService(request.getSourceService());
        notification.setReferenceType(request.getReferenceType());
        notification.setReferenceId(request.getReferenceId());
        notification.setTargetUrl(request.getTargetUrl());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());

        return notificationsServiceClient.create(notification);
    }

    public List<Notification> getNotificationsByUser(String userId) {
        return getNotificationsByUser(userId, null);
    }

    public List<Notification> getNotificationsByUser(String userId, Boolean read) {
        return notificationsServiceClient.list(userId, read);
    }

    public List<Notification> getNotificationsForCurrentAdmin(String userId, Boolean read) {
        return notificationsServiceClient.list(userId, read);
    }

    public Notification getNotificationById(String id) {
        return notificationsServiceClient.getById(id);
    }

    public Notification updateNotification(String id, Notification notification) {
        return notificationsServiceClient.update(id, notification);
    }

    public Notification markRead(String id, boolean read) {
        return notificationsServiceClient.markRead(id, read);
    }

    public void deleteNotification(String id) {
        notificationsServiceClient.delete(id);
    }
}
