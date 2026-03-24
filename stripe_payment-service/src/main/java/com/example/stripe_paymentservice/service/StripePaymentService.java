package com.example.stripe_paymentservice.service;

import com.example.stripe_paymentservice.clients.OrderServiceClient;
import com.example.stripe_paymentservice.dtos.*;
import com.example.stripe_paymentservice.model.SellerPaymentProfile;
import com.example.stripe_paymentservice.repository.SellerPaymentProfileRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.stripe.model.Transfer;
import com.stripe.param.TransferCreateParams;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final SellerPaymentProfileRepository sellerPaymentProfileRepository;
    private final OrderServiceClient orderServiceClient;
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request)
            throws StripeException {

        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new RuntimeException("orderId is required");
        }
        if (request.sellerUserId() == null || request.sellerUserId().isBlank()) {
            throw new RuntimeException("sellerUserId is required");
        }
        if (request.grossAmountCents() == null || request.grossAmountCents() <= 0) {
            throw new RuntimeException("grossAmountCents must be greater than 0");
        }
        if (request.currency() == null || request.currency().isBlank()) {
            throw new RuntimeException("currency is required");
        }

        SellerPaymentProfile seller = sellerPaymentProfileRepository.findByUserId(request.sellerUserId())
                .orElseThrow(() -> new RuntimeException("Seller payment profile not found"));

        if (!Boolean.TRUE.equals(seller.getOnboardingComplete())
                || !Boolean.TRUE.equals(seller.getChargesEnabled())
                || !Boolean.TRUE.equals(seller.getPayoutsEnabled())) {
            throw new RuntimeException("Seller is not ready to accept payments");
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.grossAmountCents())
                .setCurrency(request.currency())
                .putMetadata("orderId", request.orderId())
                .putMetadata("sellerUserId", request.sellerUserId())
                .putMetadata("platformFeeCents", String.valueOf(request.platformFeeCents()))
                .putMetadata("sellerAmountCents", String.valueOf(request.sellerAmountCents()))
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return new CreatePaymentIntentResponse(
                paymentIntent.getId(),
                paymentIntent.getClientSecret(),
                paymentIntent.getStatus()
        );
    }

    public ResponseEntity<String> handleWebhook(String payload, String signatureHeader)
            throws SignatureVerificationException {

        Event event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new RuntimeException("Unable to deserialize payment intent"));

            String orderId = paymentIntent.getMetadata().get("orderId");
            String paymentIntentId = paymentIntent.getId();

            if (orderId == null || orderId.isBlank()) {
                throw new RuntimeException("orderId metadata missing in payment intent");
            }

            orderServiceClient.markPaymentSucceeded(
                    orderId,
                    new PaymentSucceededRequest(paymentIntentId)
            );
        }

        return ResponseEntity.ok("Webhook received");
    }

    public ReleaseFundsResponse releaseFunds(ReleaseFundsRequest request) throws StripeException {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new RuntimeException("orderId is required");
        }
        if (request.sellerUserId() == null || request.sellerUserId().isBlank()) {
            throw new RuntimeException("sellerUserId is required");
        }
        if (request.sellerAmountCents() == null || request.sellerAmountCents() <= 0) {
            throw new RuntimeException("sellerAmountCents must be greater than 0");
        }
        if (request.currency() == null || request.currency().isBlank()) {
            throw new RuntimeException("currency is required");
        }

        SellerPaymentProfile seller = sellerPaymentProfileRepository.findByUserId(request.sellerUserId())
                .orElseThrow(() -> new RuntimeException("Seller payment profile not found"));

        if (seller.getStripeConnectedAccountId() == null || seller.getStripeConnectedAccountId().isBlank()) {
            throw new RuntimeException("Seller connected account is missing");
        }

        if (!Boolean.TRUE.equals(seller.getPayoutsEnabled())) {
            throw new RuntimeException("Seller payouts are not enabled");
        }

        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(request.sellerAmountCents())
                .setCurrency(request.currency())
                .setDestination(seller.getStripeConnectedAccountId())
                .putMetadata("orderId", request.orderId())
                .putMetadata("sellerUserId", request.sellerUserId())
                .build();

        Transfer transfer = Transfer.create(params);

        return new ReleaseFundsResponse(
                transfer.getId(),
                "SUCCESS"
        );
    }

    public RefundPaymentResponse refundPayment(RefundPaymentRequest request) throws StripeException {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new RuntimeException("orderId is required");
        }
        if (request.paymentIntentId() == null || request.paymentIntentId().isBlank()) {
            throw new RuntimeException("paymentIntentId is required");
        }
        if (request.amountCents() == null || request.amountCents() <= 0) {
            throw new RuntimeException("amountCents must be greater than 0");
        }

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(request.paymentIntentId())
                .setAmount(request.amountCents())
                .putMetadata("orderId", request.orderId())
                .putMetadata("reason", request.reason() == null ? "requested_by_platform" : request.reason())
                .build();

        Refund refund = Refund.create(params);

        return new RefundPaymentResponse(
                refund.getId(),
                refund.getStatus()
        );
    }
}