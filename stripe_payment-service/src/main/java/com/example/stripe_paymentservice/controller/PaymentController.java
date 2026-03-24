package com.example.stripe_paymentservice.controller;

import com.example.stripe_paymentservice.dtos.*;
import com.example.stripe_paymentservice.service.StripePaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripePaymentService stripePaymentService;

    @PostMapping("/intent")
    public CreatePaymentIntentResponse createPaymentIntent(
            @RequestBody CreatePaymentIntentRequest request
    ) throws StripeException {
        return stripePaymentService.createPaymentIntent(request);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader
    ) throws SignatureVerificationException {
        return stripePaymentService.handleWebhook(payload, signatureHeader);
    }

    @PostMapping("/release")
    public ReleaseFundsResponse releaseFunds(
            @RequestBody ReleaseFundsRequest request
    ) throws StripeException {
        return stripePaymentService.releaseFunds(request);
    }

    @PostMapping("/refund")
    public RefundPaymentResponse refundPayment(
            @RequestBody RefundPaymentRequest request
    ) throws StripeException {
        return stripePaymentService.refundPayment(request);
    }
}