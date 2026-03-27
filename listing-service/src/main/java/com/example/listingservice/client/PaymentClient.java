package com.example.listingservice.client;

import com.example.listingservice.dto.internal.SellerPaymentProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-service", url = "${application.config.payment-url}")
public interface PaymentClient {

    @GetMapping("/api/sellers/{userId}/status")
    SellerPaymentProfileDTO refreshSellerStatus(@PathVariable("userId") String userId);
}
