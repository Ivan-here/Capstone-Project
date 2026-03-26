package com.example.profileservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {

    private final WebClient webClient;
    private final String notificationsBaseUrl;

    public NotificationClient(
            WebClient webClient,
            @Value("${clients.notificationsBaseUrl}") String notificationsBaseUrl
    ) {
        this.webClient = webClient;
        this.notificationsBaseUrl = notificationsBaseUrl;
    }

    public void createNotification(NotificationRequest request) {
        webClient.post()
                .uri(notificationsBaseUrl + "/notifications")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public static class NotificationRequest {
        private String userId;
        private String actorUserId;
        private String type;
        private String title;
        private String message;
        private String sourceService;
        private String referenceType;
        private String referenceId;
        private String targetUrl;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getActorUserId() { return actorUserId; }
        public void setActorUserId(String actorUserId) { this.actorUserId = actorUserId; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getSourceService() { return sourceService; }
        public void setSourceService(String sourceService) { this.sourceService = sourceService; }

        public String getReferenceType() { return referenceType; }
        public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

        public String getReferenceId() { return referenceId; }
        public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

        public String getTargetUrl() { return targetUrl; }
        public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    }
}
