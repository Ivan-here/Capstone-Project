package com.example.followservice.client;

import com.example.followservice.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "follow-notifications-client", url = "${clients.notifications-service.url}")
public interface NotificationClient {

    @PostMapping("/notifications")
    void createNotification(@RequestBody NotificationRequest request);
}
