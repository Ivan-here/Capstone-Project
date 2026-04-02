package com.locally.orders.order_reservationservice.client;

import com.locally.orders.order_reservationservice.dtos.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notifications.service.base-url:http://localhost:8085}")
    private String notificationsBaseUrl;

    public void createNotification(NotificationRequest request) {
        restTemplate.postForLocation(
                notificationsBaseUrl + "/notifications",
                request
        );
    }
}
