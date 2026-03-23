package com.example.stripe_paymentservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnboardingLinkResponse {
    private String url;
}