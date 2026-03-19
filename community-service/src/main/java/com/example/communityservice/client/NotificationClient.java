package com.example.communityservice.client;

import com.example.communityservice.model.NotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {

    private final WebClient webClient;
    private final String notificationsBaseUrl;

    public NotificationClient(
            WebClient webClient,
            @Value("${services.notifications.base-url}") String notificationsBaseUrl
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
}
