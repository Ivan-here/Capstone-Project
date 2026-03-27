package com.example.listingservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SellerPaymentProfileDTO(
        Boolean onboardingComplete,
        Boolean chargesEnabled,
        Boolean payoutsEnabled
) {
    public boolean isReadyToReceivePayments() {
        return Boolean.TRUE.equals(onboardingComplete)
                && Boolean.TRUE.equals(chargesEnabled)
                && Boolean.TRUE.equals(payoutsEnabled);
    }
}
