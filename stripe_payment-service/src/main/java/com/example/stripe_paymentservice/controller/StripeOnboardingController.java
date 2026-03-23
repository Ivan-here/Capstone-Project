package com.example.stripe_paymentservice.controller;

import com.example.stripe_paymentservice.dtos.OnboardingLinkResponse;
import com.example.stripe_paymentservice.model.SellerPaymentProfile;
import com.example.stripe_paymentservice.service.StripeConnectService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe/onboarding")
@RequiredArgsConstructor
public class StripeOnboardingController {

    private final StripeConnectService stripeConnectService;

    @GetMapping("/return")
    public SellerPaymentProfile handleReturn(@RequestParam String sellerId) throws StripeException {
        return stripeConnectService.refreshStatus(sellerId);
    }

    @GetMapping("/refresh")
    public OnboardingLinkResponse handleRefresh(@RequestParam String sellerId) throws StripeException {
        return new OnboardingLinkResponse(
                stripeConnectService.createOnboardingLink(sellerId)
        );
    }
}