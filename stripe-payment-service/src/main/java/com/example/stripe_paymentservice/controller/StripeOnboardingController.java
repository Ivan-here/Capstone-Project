package com.example.stripe_paymentservice.controller;

import com.example.stripe_paymentservice.model.SellerPaymentProfile;
import com.example.stripe_paymentservice.service.StripeConnectService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/stripe/onboarding")
@RequiredArgsConstructor
public class StripeOnboardingController {

    private final StripeConnectService stripeConnectService;

    @GetMapping("/return")
    public RedirectView handleReturn(@RequestParam String sellerId) throws StripeException {
        SellerPaymentProfile profile = stripeConnectService.refreshStatus(sellerId);
        String redirectUrl = stripeConnectService.buildFrontendReturnUrl(
                sellerId,
                Boolean.TRUE.equals(profile.getOnboardingComplete())
        );
        return new RedirectView(redirectUrl);
    }

    @GetMapping("/refresh")
    public RedirectView handleRefresh(@RequestParam String sellerId) throws StripeException {
        return new RedirectView(stripeConnectService.createOnboardingLink(sellerId));
    }
}
