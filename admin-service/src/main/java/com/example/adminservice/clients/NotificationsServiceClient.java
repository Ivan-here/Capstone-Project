package com.example.adminservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.example.adminservice.dtos.notification.Notification;

import java.util.List;

@FeignClient(
        name = "notificationsClient",
        url = "${clients.notificationsBaseUrl}"
)
public interface NotificationsServiceClient {

    @PostMapping("/notifications")
    Notification create(@RequestBody Notification notification);

    @GetMapping("/notifications")
    List<Notification> list(
            @RequestParam("userId") String userId,
            @RequestParam(value = "read", required = false) Boolean read
    );

    @GetMapping("/notifications/{id}")
    Notification getById(@PathVariable("id") String id);

    @PutMapping("/notifications/{id}")
    Notification update(
            @PathVariable("id") String id,
            @RequestBody Notification notification
    );

    @PatchMapping("/notifications/{id}/read")
    Notification markRead(
            @PathVariable("id") String id,
            @RequestParam("read") boolean read
    );

    @DeleteMapping("/notifications/{id}")
    void delete(@PathVariable("id") String id);
}
