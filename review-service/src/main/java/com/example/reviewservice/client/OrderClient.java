package com.example.reviewservice.client;

import com.example.reviewservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-reservation-service", url = "${clients.orderReservationBaseUrl}", configuration = FeignConfig.class)
public interface OrderClient {

    // Returns true ONLY if the order is COMPLETED and belongs to this user
    @GetMapping("/internal/orders/{orderId}/verify")
    boolean verifyOrderCompletion(@PathVariable("orderId") String orderId, @RequestParam("userId") String userId);
}