package com.example.stripe_paymentservice.clients;

import com.example.stripe_paymentservice.dtos.BusinessProfile;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "profileClient",
        url = "${clients.profileBaseUrl}"
)
public interface ProfileServiceClient {

    @GetMapping("/internal/profiles/{userId}/business")
    BusinessProfile getBusinessProfileInternal(@PathVariable("userId") String userId);

    @Data
    class BusinessProfileDto {
        private String id;
        private String userId;
        private String businessType;
        private String businessName;
        private String address;
        private String email;
        private boolean verified;
    }

}
