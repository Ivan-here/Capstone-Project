package com.example.notificationsservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.notificationsservice.model.Notification;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
}