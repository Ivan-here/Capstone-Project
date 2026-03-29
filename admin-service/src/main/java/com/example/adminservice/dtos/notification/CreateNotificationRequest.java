package com.example.adminservice.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    private String userId;
    private String actorUserId;
    private String type;
    private String title;
    private String message;
    private String sourceService;
    private String referenceType;
    private String referenceId;
    private String targetUrl;
}
