package com.example.listingservice.client;

import com.example.listingservice.dto.internal.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications-service-client", url = "${application.config.notifications-url}")
public interface NotificationClient {

    @PostMapping("/notifications")
    void createNotification(@RequestBody NotificationRequest request);
}
