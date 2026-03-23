package com.example.stripe_paymentservice.controller;

import com.example.stripe_paymentservice.dtos.CreateConnectedAccountRequest;
import com.example.stripe_paymentservice.dtos.OnboardingLinkResponse;
import com.example.stripe_paymentservice.model.SellerPaymentProfile;
import com.example.stripe_paymentservice.service.StripeConnectService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerPaymentController {

    private final StripeConnectService stripeConnectService;

    @PostMapping("/connect-account")
    public SellerPaymentProfile createConnectedAccount(@RequestBody CreateConnectedAccountRequest request)
            throws StripeException {
        return stripeConnectService.createConnectedAccount(request.getUserId());
    }

    @PostMapping("/{userId}/onboarding-link")
    public OnboardingLinkResponse createOnboardingLink(@PathVariable String userId)
            throws StripeException {
        return new OnboardingLinkResponse(
                stripeConnectService.createOnboardingLink(userId)
        );
    }

    @GetMapping("/{userId}/status")
    public SellerPaymentProfile refreshStatus(@PathVariable String userId)
            throws StripeException {
        return stripeConnectService.refreshStatus(userId);
    }

    @GetMapping("/{userId}")
    public SellerPaymentProfile getSellerPaymentProfile(@PathVariable String userId) {
        return stripeConnectService.getByUserId(userId);
    }
}